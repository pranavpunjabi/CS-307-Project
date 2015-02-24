from myApp import myApp
from flask import Flask, jsonify, request, abort, make_response
from models import db, User

@myApp.route('/server/signup', methods=['POST'])
def signup():
	newuser = User(request.json['firstname'], request.json['lastname'], request.json['email'], request.json['password'])
	db.session.add(newuser)
	db.session.commit()
	return 'DONE'

@myApp.route('/testdb', methods=['GET'])
def testdb():
  print "hey"
  if db.session.query("1").from_statement("SELECT 1").all():
    return 'It works.'
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
