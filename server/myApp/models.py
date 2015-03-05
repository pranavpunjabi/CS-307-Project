from flask.ext.sqlalchemy import SQLAlchemy
from werkzeug import generate_password_hash, check_password_hash

db = SQLAlchemy()

class User(db.Model):
	__tablename__ = 'students'
	id = db.Column(db.Integer, primary_key = True)
	firstname = db.Column(db.String(100))
	lastname = db.Column(db.String(100))
	email = db.Column(db.String(100))
	pwdhash = db.Column(db.String(54))
	ifTutor = db.Column(db.String(5))
	
	def __init__(self, firstname, lastname, email, password):
		self.firstname = firstname.title()
		self.lastname = lastname.title()
		self.email = email.lower()
		self.set_password(password)
		self.ifTutor = 0

	def set_password(self, password):
		self.pwdhash = generate_password_hash(password)

	def check_password(self, password):
		return check_password_hash(self.pwdhash, password)

class Subjects(db.Model):
	__tablename__ = 'subjects'
	subject = db.Column(db.String(100), primary_key = True)
	ids = db.Column(db.String(100))
	def __init__(self, subject, ids):
		self.ids = ids.title()
		self.subject = subject.title()

class Tutor(db.Model):
	__tablename__ = 'tutors'
	id = db.Column(db.Integer, primary_key = True)
	subjects = db.Column(db.String(100))
	def __init__(self, id, subjects):
		self.id = id.title()
		self.subjects = subjects.title()
