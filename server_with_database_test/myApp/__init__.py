from flask import Flask#, jsonify, request, abort, make_response, url_for

myApp = Flask(__name__)
myApp.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://sharoons:password@localhost/development'

from models import db

db.init_app(myApp)

#app.config.from_object('config')
#db = SQLAlchemy(myApp)

from myApp import views

