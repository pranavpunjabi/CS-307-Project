from flask import Flask
#from flask.ext.socketio import SocketIO, emit

myApp = Flask(__name__)
myApp.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://sharoons:password@localhost/GetGuru'
myApp.secret_key = 'development key'
from models import db
#myApp.config['SECRET_KEY'] = 'secret!'
#socketio = SocketIO(myApp)
#if __name__ == '__main__':
#	socketio.run(myApp)

db.init_app(myApp)

from myApp import views

