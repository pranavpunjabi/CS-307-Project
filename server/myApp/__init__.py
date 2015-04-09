from flask import Flask

myApp = Flask(__name__)
myApp.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://ankush:password@localhost/GetGuru'
myApp.secret_key = 'development key'
from models import db

db.init_app(myApp)

from myApp import views

