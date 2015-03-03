from myApp import myApp
from flask import Flask, jsonify, request, abort, make_response, session
from models import db, User, Subjects, Tutor


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

	inst  = Subjects.query.filter(Subjects.subject == request.json['subject']).order_by(Subjects.subject).first()
     	if inst is None:
     	     newid = request.json['id']
     	     newsubject = Subjects(request.json['subject'], newid)
     	     db.session.add(newsubject)
     	     db.session.commit()
     	     return jsonify({'return':'success'})
     	else:
     	     idlist = inst.ids.split(',')
     	     idexist = 0
     	     for idval in idlist:
     	          if (idval == request.json['id']):
     	               idexist = 1
     	               break
     	     if (idexist == 0):
     	          newid = inst.ids + "," + request.json['id']
     	          newsubject = Subjects(request.json['subject'], newid)
     	          db.session.merge(newsubject)
     	          db.session.commit()
     	          return jsonify({'return':'success'})
     	     else: 
     	          return jsonify({'return':'duplicate id'})

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
