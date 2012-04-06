#!/bin/bash
python genBuiltin.py
dot tree.dot -Tpng > tree.png

# put tree.png on public folder on the trottier server
#scp tree.png mimi:~/public_html/data/tree.png
#ssh lab6-6 'cd ~/public_html/data/; chmod +R tree.png'