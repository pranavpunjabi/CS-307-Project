from myApp import myApp
from flask import Flask, jsonify, request, abort, make_response, session
from models import db, User, Subjects, Tutor

@myApp.route('/server/test', methods=['GET'])
def testing():
	firstName = request.json['firstName']
	student = User.query.filter_by(firstname = request.json['firstName']).all()
	print student[1].id
	print student[2].id
	print student[0].id
	return jsonify({'return':'test'})

@myApp.route('/server/index', methods=['GET', 'POST'])
def check():
	if request.json['requestType'] == 'signIn':
		return signin()
	elif request.json['requestType'] == 'signUp':
		return signup()
	else:
		return jsonify({'return':'noSuccess'})

@myApp.route('/server/signin', methods=['GET', 'POST'])
def signin():
	student = User.query.filter_by(email = request.json['email']).first()
	if student and student.check_password(request.json['password']):
		session['email'] = request.json['email']
		id = student.id
		return jsonify({'return':'success','id':id})
	else:
		return jsonify({'return':'invalid email and password'})
		

@myApp.route('/server/signup', methods=['POST'])
def signup():
	student = User.query.filter_by(email = request.json['email']).first()
	if student:
		return jsonify({'return':'noSuccess'})
	newuser = User(request.json['firstName'], request.json['lastName'], request.json['email'], request.json['password'])
	db.session.add(newuser)
	db.session.commit()
	session['email'] = newuser.email
	id = newuser.id
	return jsonify({'return':'success','id':id})

@myApp.route('/server/tutor', methods=['POST'])
def maketutor():
	tutor = User.query.filter_by(id = request.json['id']).first()
	if tutor is None:
		return jsonify({'return':'student with this ID has not been registered'})
	else:
		tutor.ifTutor = 1
		db.session.commit()
		newTutor = Tutor(request.json['id'], '')
		db.session.add(newTutor)
		db.session.commit()
		return jsonify({'return':'success'})

@myApp.route('/server/addSubjects', methods=['POST'])
def makesubjects():
	#adding to tutor table here
	id = request.json['id']
	tutor = Tutor.query.filter_by(id = request.json['id']).first()
	if tutor is None:
		return jsonify({'return':'error'})
	else:
		eraseTutor = Tutor(id, '')
		db.session.merge(eraseTutor)
		db.session.commit()
		#erase end
		allSubjects = ""
		i = 0
		for subject in request.json['subjects']:
			if (i == 0):
				allSubjects = subject["subject"]
				i = i + 1
			else:
				i = i+1
				allSubjects = allSubjects + "," + subject["subject"]
		myTutor = Tutor(id, allSubjects)
		db.session.merge(myTutor)
		db.session.commit()
	#finished adding to tutor table

	subjects = Subjects.query.all()
	for subject in subjects:
		ids = ""
		idlist = subject.ids.split(',')
		for idval in idlist:
			if idval != request.json['id']:
				if not ids:
					ids = idval
				else:
					ids = ids + "," + idval
		subject.ids = ids
		if not ids:
			db.session.delete(subject)
		db.session.commit()
	for subject in request.json['subjects']:
		sub = Subjects.query.filter_by(subject = subject["subject"]).first()
		if sub is None:
			newID = request.json['id']
			newSubject = Subjects(subject["subject"],newID)
			db.session.add(newSubject)
			db.session.commit()
		else:
			if not sub.ids:
				sub.ids = request.json['id']
			else:
				sub.ids = sub.ids + "," + request.json['id']
			db.session.commit()
	return jsonify({'return':'success','id':request.json['id']})	

@myApp.route('/server/getSubjects', methods=['GET'])
def getsubjects():
	tutor = Tutor.query.filter_by(id = request.json['id']).first()
	if tutor is None:
		return jsonify({'return':'no success'})
	else:
		subjectList = tutor.subjects.split(',')
		return jsonify({'return':subjectList})

#@myApp.route('/server/addShortList', methods = ['GET','POST'])
#def addShortList():
#	id = request.json['id']
#	shortlist = Students.shortlist
#	print shortlist

@myApp.route('/testdb', methods=['GET'])
def testdb():
  print "hey"
  if db.session.query("1").from_statement("SELECT 1").all():
    return jsonify({'return':'success'})
  else:
    return jsonify('Something is broken.')

@myApp.route('/server/api/v1.0/users', methods=['GET'])
def get_users():
        return jsonify({'users': users})

@myApp.route('/server/api/v1.0/users/<int:user_id>', methods=['GET'])
def get_user_id(user_id):
        user = [user for user in users if user['id'] == user_id]
        if len(user) == 0:
                abort(404)
        return jsonify({'users': user[0]})

@myApp.route('/server/api/v1.0/users/<user_location>', methods=['GET'])
def get_user_location(user_location):
        user = [user for user in users if user['location'] == user_location]
        if len(user) == 0:
                abort(404)
        return jsonify({'users': user[0]})

@myApp.route('/server/api/v1.0/users', methods=['POST'])
def create_user():
        if not request.json or not 'name' in request.json:
                abort(400)
        user = {
                'id': users[-1]['id'] + 1,
                'name': request.json['name'],
                #'location': request.json.get('location', ""),
                'location': request.json['location'],
                'isTutor': False
        }
        users.append(user)
        return jsonify({'user': user}), 201

@myApp.errorhandler(404)
def not_found(error):
        return make_response(jsonify({'error': 'Not found'}), 404)
