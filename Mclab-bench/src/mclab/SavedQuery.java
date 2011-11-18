package mclab;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Text;

public class SavedQuery{
	String name;
	@Id	Long id;
	Text content;
	public SavedQuery(String name, String content){
		this.name=name;
		this.content=new Text(content);
	}
	public String getName() {
		return name;
	}
	public Text getContent() {
		return content;
	}
	public SavedQuery() {
	}
}
