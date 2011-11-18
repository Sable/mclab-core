from __future__ import with_statement
#!/usr/bin/env python
import cgi
import datetime
import wsgiref.handlers
import xml.dom.minidom
import BeautifulSoup
from BSXPath import  BSXPathEvaluator
from StringIO import StringIO
import urllib2
import datetime
from google.appengine.ext import db
from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.api import urlfetch
from google.appengine.api import taskqueue
from google.appengine.ext import blobstore
from pygments import highlight
from pygments.lexers import MatlabLexer, XmlLexer
from pygments.formatters import HtmlFormatter
import logging
from google.appengine.api import files



tag_list=['2d', '3d', 'acousticsadaboost', 'aerospace', 'algorithm', 'algorithmic trading', 'am', 'analysis', 'animation', 'annotation', 'antenna',  'approximation', 'area', 'array', 'arrow', 'article', 'artificial intelligence', 'ask', 'audio', 'audio processing', 'automotive', 'avi', 'awgn', 'ball', 'basic kalman filter', 'bessel', 'binary', 'bioinformatics', 'biotech', 'bpsk','cell array', 'centroid', 'chaos', 'chemistry', 'circle', 'class', 'classification', 'clustering', 'code', 'coding theory', 'color', 'colormap', 'communications', 'compare', 'compression', 'computer vision', 'contour', 'control', 'control design', 'conversion', 'convert', 'converter', 'convolution', 'coordinates', 'coupling', 'course', 'csv', 'cuda', 'curve', 'data', 'data exploration', 'database',  'demodulation', 'denoising', 'derivative', 'detection', 'dicom', 'digitize', 'display', 'distance', 'distribution', 'drives', 'dsp', 'dti', 'earth science', 'edge detection', 'electromagnetic', 'ellipse', 'em', 'embedded code', 'entropy', 'envelope', 'error', 'example', 'excel', 'export', 'extract', 'face', 'face detection', 'fft', 'figure', 'file', 'files', 'filter', 'filter analysis', 'filter design', 'filtering', 'finance', 'find', 'fit', 'fractal', 'fun', 'function', 'game', 'games', 'gaussian', 'genetic algorithm', 'geometry', 'gps', 'graph', 'graph theory', 'graphics', 'grid', 'guide', 'histogram', 'html', 'image', 'image analysis', 'image processing', 'image registration', 'image segmentation', 'import', 'information theory', 'instrument driver', 'interpolation', 'intersection', 'inverter', 'java', 'kalman', 'kalman filter', 'kmeans', 'latex', 'ldpc', 'line', 'linear', 'linear algebra', 'linear regression', 'lms', 'machine learning', 'map', 'mapping', 'mathematics', 'matlab', 'matrix', 'mean', 'measurement', 'medical', 'mesh', 'mex', 'microwave', 'mimo', 'model', 'modeling', 'modulation', 'mri', 'music', 'mutual information', 'netcdf', 'neural network', 'neuroscience', 'noise', 'nonlinear', 'numerical analysis', 'ofdm', 'oop', 'optics', 'optimization', 'order', 'parallel', 'patch', 'pattern recognition', 'pca', 'pdf', 'physical modeling', 'physics', 'pid', 'plot', 'plotting', 'polygon', 'potw', 'power electronics', 'power system', 'probability', 'pwm', 'random', 'read', 'real time', 'reality', 'regression', 'rf', 'rgb', 'robotics', 'rs232', 'save', 'scan', 'search', 'segmentation', 'self_rating', 'sequence', 'signal processing', 'simscape', 'simulation', 'simulink', 'solve', 'sonnet', 'sort', 'sound', 'space', 'speech', 'spline', 'standard deviation', 'statistics', 'stl', 'string', 'struct', 'structure', 'stub', 'subplot', 'surface', 'system identification', 'table', 'test', 'text', 'time', 'time series', 'toolbox', 'trajectory', 'tutorial', 'undocumented', 'utilities', 'utility', 'vector', 'video', 'virtual', 'visualization', 'webinar', 'wireless', 'write', 'xml', 'zoom']

class FileItem(db.Model):
  blob = db.BlobProperty()
  name = db.StringProperty()

class Bench(db.Model):
  author = db.StringProperty()
  xml = db.TextProperty()
  name = db.StringProperty()
  date = db.DateTimeProperty()
  link = db.StringProperty()
  tags = db.ListProperty(str)
  files = db.ListProperty(str)
  source_blob = db.StringProperty()
  source_name = db.StringProperty()
  def __str__(self):
    return str(self.key())+self.link+str([FileItem.get(x).name for x in self.files])
  
class MainPage(webapp.RequestHandler):
  def showXML(self, project):
    code = project.xml
    f= highlight(code, XmlLexer(), HtmlFormatter(linenos=True, noclasses=False, lineanchors='line'))
    res = '''<html><head><style type="text/css">%s</style>
    <script type="text/javascript"
    src="http://ajax.googleapis.com/ajax/libs/jquery/1.5.1/jquery.min.js"></script>
    </head><body>%s</body></html>'''% (HtmlFormatter().get_style_defs(''),f)
    self.response.out.write(res)

  def download(self, project):
    project_name = project
    logging.debug('Handling project download'+ project_name)
    project_key=db.Key.from_path('Bench', project_name)
    p = Bench.get_or_insert(key_name=project_name)
    if p.link is None:
      p.link="http://www.mathworks.com/matlabcentral/fileexchange/"+project_name
    logging.debug('link is '+ p.link)
    if p.source_name is not None:
      return True
    content = ""
    url = urlfetch.fetch(url=p.link+"?controller=file_infos&download=true", deadline=30)

    if url.status_code != 200:
        logging.debug('status code '+ str(url.status_code))
        return False
    content = url.content
    file_name = files.blobstore.create(mime_type='application/octet-stream')
    
    # Open the file and write to it
    with  files.open(file_name, 'a') as f:
      f.write(content)
      
      # Finalize the file. Do this before attempting to read it.
    files.finalize(file_name)

    p.source_blob = file_name
    p.source_name = url.final_url;
    p.put()
    logging.debug('Downloaded '+ p.link)
    return True

  def get(self):
    project_name = self.request.get('project')
    if not self.download(project_name):
        self.response.out.write("Cannot download the project")
        return
    file_name = self.request.get('file')
    project_key=db.Key.from_path('Bench', project_name)
    project = Bench.get(project_key)
    if not file_name:
      self.showXML(project)
      return

    blob_key = files.blobstore.get_blob_key(project.source_blob)
    pf =  blobstore.BlobReader(blob_key)
    logging.debug("Opening zipfile file ")
    content = ""
    if project.source_name.endswith(".zip"):
      from zipfile import ZipFile

      z = ZipFile(pf,'r')
      logging.debug(file_name+ " list:" + str(z.namelist())) 
      for x in z.namelist():
        if file_name.endswith(project_name+"/" +x):
          content = z.read(x)
      if content == "":
        self.response.out.write("File not found in the archive")

      z.close()
      pf.close()

    elif project.url.endswith(".m"):
      content = pf.read()
      pf.close()

    else:
      self.response.out.write("File not found in the archive")
      pf.close()
      return

    lines=map(int,self.request.get('lines').split())
    code = content
    f= highlight(unicode(code,errors='ignore'), MatlabLexer(), HtmlFormatter(linenos=True, noclasses=False, lineanchors='line', hl_lines=lines))
    res = '''<html><head><style type="text/css">%s</style>
    <script type="text/javascript"
    src="http://ajax.googleapis.com/ajax/libs/jquery/1.5.1/jquery.min.js"></script>
    <script>$(document).ready(function(){
    $('.n, .nf').wrap(function(){return '<a href="http://java.mclab-bench.appspot.com/find?project=%s&term='+$(this).text()+ '"/>';});
    });</script>
    </head><body>%s</body></html>'''% (HtmlFormatter().get_style_defs(''),project_name,f)
    self.response.out.write(res)

    # greetings = db.GqlQuery("SELECT * "
    #                         "FROM Greeting "
    #                         "ORDER BY date DESC LIMIT 10")
    # for greeting in greetings:
    #   if greeting.author:
    #     self.response.out.write('<b>%s</b> wrote:' % greeting.author.nickname())
    #   else:
    #     self.response.out.write('An anonymous person wrote:')
    #   self.response.out.write('<blockquote>%s</blockquote>' %
    #                           cgi.escape(greeting.content))

  def post(self):
    if not users.get_current_user():
      self.redirect(users.create_login_url(self.request.uri))
    links = []
    tag='physics'

    
    for tag in tag_list:
      for p in range(1,11):
        link='http://www.mathworks.com/matlabcentral/fileexchange/?sort=downloads&page='+str(p)+'&term=license:bsd+tag:%22'+tag+'%22'
        taskqueue.add(url='/downloadPage', params={'key': link, 'tag':tag})

    self.redirect('/')


class DownloadProject(webapp.RequestHandler):
  def get(self):
    return self.post()

  def post(self):
    project_name = self.request.get("project")
    logging.debug('Handling project download'+ project_name)
    project_key=db.Key.from_path('Bench', project_name)
    p = Bench.get_or_insert(key_name=project_name)
    if p.link is None:
      p.link="http://www.mathworks.com/matlabcentral/fileexchange/"+project_name
    logging.debug('link is '+ p.link)
    if p.source_name is not None:
      return
    content = ""
    url = urllib2.urlopen(p.link+"?controller=file_infos&download=true")
    content = url.read()
    url.close()
    file_name = files.blobstore.create(mime_type='application/octet-stream')

    # Open the file and write to it
    with  files.open(file_name, 'a') as f:
      f.write(content)
      
      # Finalize the file. Do this before attempting to read it.
    files.finalize(file_name)

    p.source_blob = file_name
    p.source_name = url.url;
    p.put()
    logging.debug('Downloaded '+ p.link)

class Download(webapp.RequestHandler):
  def post(self): # should run at most 1/s
    link = self.request.get('key')
    tag = self.request.get('tag')
    rpc = urlfetch.create_rpc()
    urlfetch.make_fetch_call(rpc, link)
    benchs={}
    try:
      result = rpc.get_result()
    except:
      pass
    if result.status_code == 200:
      doc = BSXPathEvaluator(result.content) 
      for project in doc.getItemList("//tr[td/span/@class='tags']"):
        l = unicode(doc.getFirstItem("./td/span[@class='title']/a/@href", project))
        p = Bench.get_or_insert(
          author=unicode(doc.getFirstItem("./td/span[@class='author']/a/text()", project)),
          name=unicode(doc.getFirstItem("./td/span[@class='title']/a/text()", project)),
          #date=datetime.datetime(doc.getFirstItem("./td/span[@class='date']/text()", project)),
          link= l,
          tags=list([unicode(x) for x in doc.getItemList("./td/span[@class='tags']/a/text()", project)]+[tag]),
          key_name=l[l.rfind("/")+1:])
        benchs[l[l.rfind("/")+1:]]=p
        p.put()
        if len(p.files)==0:
          taskqueue.add(url='/downloadProject', params={'key': p.key()})
    else:
      print "error", link, result
    try:
      urllib2.urlopen("http://java.latest.mclab-bench/parse_all/")
    except:
      pass

class List(webapp.RequestHandler):
  def get(self):
    self.response.out.write(str(Bench.all().count()))
    self.response.out.write(str([str(x) for x in Bench.all()]))

application = webapp.WSGIApplication([
  ('/', MainPage),
  ('/downloadPage', Download),
  ('/downloadProject', DownloadProject),
  ('/list', List),
], debug=False)


def main():
  wsgiref.handlers.CGIHandler().run(application)
  logging.getLogger().setLevel(logging.DEBUG)


if __name__ == '__main__':
  main()
