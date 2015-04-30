from myApp import myApp
from flask import Flask, jsonify, request, abort, make_response, session
from models import db, User, Subjects, Tutor, Rating
from sqlalchemy import update
from geopy.geocoders import Nominatim
import collections
from flask.ext.httpauth import HTTPBasicAuth
auth = HTTPBasicAuth()
geolocator = Nominatim()

@auth.verify_password
def verify_password(email, password):
    user = User.query.filter_by(email = email).first()
    #password1 = str(password)
    if user and user.check_password(password):
        return True
    #g.user = user
    return False

@myApp.route('/server/test', methods=['GET'])
def testing():
	#student = User.query.filter(User.firstname == request.json['firstName'], User.lastname == request.json['lastName']).all()
	sub = request.json['subject']
	print sub
	newSub = "%" + sub + "%"
	print newSub
	student = Tutor.query.filter(Tutor.subjects.like(newSub)).all()
	print len(student)
	return jsonify({'return':'test'})
@myApp.route('/server/locSearch', methods=['GET'])
def locSearch():
	print request.query_string
	print request.url
	#making temporary variables since not sure how the client will pass them
	#print request.json['latitude']
	#latitude = request.json['latitude']
	#longitude = request.json['longitude']
	latitude = request.args.get('latitude')
	longitude = request.args.get('longitude')
	print 'here'
	print latitude
	print type(latitude)
	#tempLoc = geolocator.reverse("40.425869, -86.908066")
	tempLoc = geolocator.reverse("%f, %f"%(float(latitude),float(longitude)))
	print(tempLoc.address)
	tempZip = tempLoc.raw["address"]["postcode"]
	tutor = Tutor.query.filter_by(location = tempZip).all()	
	if(len(tutor) == 0):
		return jsonify ({'return':'noSuccess'})
	else: 
		tutors = []
		for myTutor in tutor:
			tutors.append({"id":myTutor.id, "location":myTutor.location, "subjects":myTutor.subjects})
		#	{"id":tutor[0].id, "location":tutor[0].location, "subjects":tutor[0].subjects}
		#tutors.append({"id":tutor[0].id, "location":tutor[0].location, "subjects":tutor[0].subjects})
		return jsonify({'return':tutors})
	return jsonify ({'return':'success'})

@myApp.route('/server/search', methods=['GET'])
def search():
	finalTutors = []
	jusChecking = request.args.get('zipcode')
	if jusChecking is None:
		latitude = request.args.get('latitude')
        	longitude = request.args.get('longitude')
        	tempLoc = geolocator.reverse("%f, %f"%(float(latitude),float(longitude)), timeout=10)
        	if tempLoc.address is None:
        		return jsonify({'return':'error'})
		tempZip = tempLoc.raw["address"]["postcode"]
		#print tempZip
	else:
		tempZip = jusChecking
	tutor = []
	rating = 0
	rate = request.args.get('rating')
	
	if not rate:
		rating = 0
	else:
		rating = int(rate)
	subjects = request.args.getlist('subject')
	pincodes = []
	pincodes = pincodes + [int(tempZip)]
	pincodes = pincodes + [int(tempZip) + 1]
	pincodes = pincodes + [int(tempZip) - 1]
	if not subjects:
		tutor = Tutor.query.filter(Tutor.location.in_(pincodes), Tutor.avgRatings >= rating).all()
	elif len(subjects) == 1:
		sub = "%" + request.args.get('subject') + "%"
		tutor = Tutor.query.filter(Tutor.subjects.like(sub), Tutor.location.in_(pincodes), Tutor.avgRatings >= rating).all()
	else:
		idlist = []
		for subject in subjects:
			ids = Subjects.query.filter(Subjects.subject == subject).first() #get the row with the subject
			if(ids is None): #if none of the subjects searched are there
				return jsonify({'return':'noSuccess'})
			curr_ids = ids.ids.split(',') #split ids into array
			idlist.extend(curr_ids) #put ids into the list

		idslist =  [x for x, y in collections.Counter(idlist).items() if y > 1] #get the intersection of the ids
		if not idslist: #if no common subjects
			return jsonify({'return':'noSuccess'}) #check if no match for multiple subjects
		tutor = Tutor.query.filter(Tutor.location.in_(pincodes), Tutor.avgRatings >= rating, Tutor.id.in_(idslist)).all() #
	if(len(tutor) == 0):
		return jsonify ({'return':'noSuccess'})
	else:
		for myTutor in tutor:
			student = User.query.filter_by(id = myTutor.id).first()
			#print student.id
			finalTutors.append({"firstName":student.firstname, "lastName":student.lastname, "id":student.id, "location":myTutor.location, "subjects":myTutor.subjects})
		return jsonify({'return':finalTutors})

@myApp.route('/server/nameS', methods = ['GET'])
def namesearch():
     retName = []
     firstname = "%" + request.args.get('firstname') + "%"
     lastname = "%" + request.args.get('lastname') + "%"
     allName = User.query.filter(User.firstname.like(firstname), User.lastname.like(lastname)).all()
     count = User.query.filter(User.firstname.like(firstname), User.lastname.like(lastname)).count()
     if allName:
          for i in range(0,count):
               print allName[i].ifTutor
               if (allName[i].ifTutor == 1):
                    retName.append({"id":allName[i].id})
     else:
          return jsonify({'return':'No such tutors'})
     return jsonify({'return':retName})

@myApp.route('/server/editInfo', methods = ['POST'])
def editinfo():
     student = User.query.filter_by(id = request.json['id']).first()
     if student is None:
          return jsonify({'return':'student with this ID has not been registered'})
     if (request.json['lastname'] != ""):
          User.query.filter_by(id = request.json['id']).update(dict(lastname=request.json['lastname']))
     if (request.json['firstname'] != ""):
          User.query.filter_by(id = request.json['id']).update(dict(firstname=request.json['firstname']))
     if (request.json['email'] != ""):
     	  User.query.filter_by(id = request.json['id']).update(dict(email=request.json['email']))
     if (request.json['password'] != ""):
          User.set_password(student,request.json['password'])
          User.query.filter_by(id = request.json['id']).update(dict(pwdhash=student.pwdhash))
     db.session.commit()
     return jsonify({'return':'success','id':student.id,'firstname':student.firstname,'lastname':student.lastname,'email':student.email})

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
		return jsonify({'return':'success','id':student.id,'firstname':student.firstname,'lastname':student.lastname,'email':student.email,'ifTutor':student.ifTutor})
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
	return jsonify({'return':'success','id':newuser.id,'firstname':newuser.firstname,'lastname':newuser.lastname,'email':newuser.email})

@myApp.route('/server/tutor', methods=['POST'])
def maketutor():
	tutor = User.query.filter_by(id = request.json['id']).first()
	if tutor is None:
		return jsonify({'return':'student with this ID has not been registered'})
	else:
		tutor.ifTutor = 1
		db.session.commit()
		newTutor = Tutor(request.json['id'],request.json['location'], '',0,0)
		db.session.add(newTutor)
		db.session.commit()
		return jsonify({'return':'success'})

@myApp.route('/server/untutor',methods=['POST'])
def unmaketut():
     tutuser = User.query.filter_by(id = request.json['id']).first()
     if tutuser is None:
          return jsonify({'return':'student with this ID has not been registered'})
     elif (tutuser.ifTutor == 0):
          return jsonify({'return':'student was NEVER a tutor'})
     else:
          str = ","
          subjects = Subjects.query.all()
          favorites = User.query.all()
          tutuser.ifTutor = 0
          tutor = Tutor.query.filter_by(id = request.json['id']).first()
          count = Rating.query.filter_by(tutID = request.json['id']).count()
          rate = Rating.query.filter_by(tutID = request.json['id']).all()
          for i in range(0,count):
               db.session.delete(rate[i])         
          for subject in subjects:
               ids = ""
               idlist = subject.ids.split(',')
               index = 0
               indexCount = -1
               for idval in idlist:
                    if idval == request.json['id']:
                         indexCount = index
                         break
                    else:
                          index += 1
               if (indexCount > -1):
                    idlist.pop(indexCount)
               newid = str.join(idlist)
               Subjects.query.filter_by(subject = subject.subject).update(dict(ids=newid))
          for favorite in favorites:
               if(favorite.favorites != None):
                    favlist = favorite.favorites.split(',')
                    newindex = 0
                    newindexCount = -1
                    for favval in favlist:
                         if favval == request.json['id']:
                              newindexCount = newindex
                              break
                         else:
                              newindex += 1
                    if(newindexCount > -1):
                         favlist.pop(newindexCount)
                    newfav = str.join(favlist)
                    User.query.filter_by(favorites = favorite.favorites).update(dict(favorites=newfav))
          db.session.delete(tutor)
          db.session.commit()
          return jsonify({'return':'success'})

@myApp.route('/server/delete', methods=['POST'])
def delUser():
     student = User.query.filter_by(id = request.json['id']).first()
     if student.ifTutor == 1:
          unmaketut()          
     db.session.delete(student)
     db.session.commit()
     return jsonify({'return':'user deleted'})

@myApp.route('/server/addtutorlocation', methods=['POST'])
@auth.login_required
def addlocation():
	tutor = Tutor.query.filter(Tutor.id == request.json['id']).first()
	if tutor is None:
		return jsonify({'return':'noSuccess'})
	tutor.location = request.json['location']
	db.session.commit()
	return jsonify({'return':'success','id':request.json['id']})

@myApp.route('/server/addfavorites', methods=['POST'])
def addfav():
	student = User.query.filter(User.id == request.json['studentID']).first()
	if student is None:
		return jsonify({'return':'noSuccess'})
	if not student.favorites:
		student.favorites = request.json['tutorID']
	else:
		favorites = student.favorites.split(',')
		for fav in favorites:
			if fav == request.json['tutorID']:
				return jsonify({'return':'tutor already a favorite'})
		student.favorites = str(student.favorites) + "," + str(request.json['tutorID'])
	db.session.commit()
	return jsonify({'return':'Success','studentID':request.json['studentID']})	
	
@myApp.route('/server/getfavorites', methods=['GET'])
def getfav():
	finalTutors = []
	student = User.query.filter(User.id == request.args.get('studentID')).first()
	if not student.favorites:
		return jsonify({'return':'noSuccess'})
	tutors = student.favorites.split(',')
	tutor = Tutor.query.filter(Tutor.id.in_(tutors)).all()

	for myTutor in tutor:
		student = User.query.filter_by(id = myTutor.id).first()
		finalTutors.append({"id":myTutor.id, "location":myTutor.location, "subjects":myTutor.subjects, "firstName":student.firstname, "lastName":student.lastname})
	return jsonify({'return':finalTutors})

@myApp.route('/server/getTutor', methods=['GET'])
def tutdetails():
     tut = User.query.filter_by(id = request.args.get('id')).first()
     if tut is None:
          return jsonify({'return':'noSuccess'})
    
     if tut.ifTutor == 0:
		return jsonify({'return':'tutor with this ID has not been registered'})
     else:
	     tutor = Tutor.query.filter_by(id = request.args.get('id')).first()
	     subjectsA = tutor.subjects.split(',')
	     print type(tutor.avgRatings)
	     return jsonify({'return':'success','id':tut.id,'firstname':tut.firstname,'lastname':tut.lastname,'rating':tutor.avgRatings,'email':tut.email, 'subjects':subjectsA})
@myApp.route('/server/getStudent', methods=['GET'])
def studetails():
     tut = User.query.filter_by(id = request.args.get('id')).first()
     if tut is None:
                return jsonify({'return':'noSuccess'})
     else:
             	return jsonify({'return':'success','id':tut.id,'firstname':tut.firstname,'lastname':tut.lastname,'email':tut.email})



@myApp.route('/server/getTutinfo', methods=['GET'])
def tutInfo():
     newtut = Tutor.query.filter_by(id = request.args.get('id')).first()
     tutorInfo = []
     if newtut is None:
          return jsonify({'return':'noSuccess'})
     else:
          rate = Rating.query.filter_by(tutID = request.args.get('id')).all()
          count = Rating.query.filter_by(tutID = request.args.get('id')).count()
          #print count
          #ratelist = " "
          for i in range(0,count):
                tutorInfo.append({'rating':(rate[i].ratings) , 'review':(rate[i].reviews)})
          #ratelist = list(ratelist)
          #print ratelist
          #print reviewlist
          return jsonify({'return':tutorInfo , 'avgRatings':newtut.avgRatings })

@myApp.route('/server/ratings',methods=['POST'])
def ratetut():
     newrate = Rating(request.json['tutID'], request.json['stuID'], request.json['ratings'], request.json['reviews'])    
     tutor = Tutor.query.filter_by(id = request.json['tutID']).first()
     
     if newrate.tutID == newrate.stuID:
          return jsonify({'return':'Tutor cant rate himself'})
     elif float(newrate.ratings) > 5:
          return jsonify({'return':'Please give a rating between 0 to 5'})
     elif float(newrate.ratings) < 0:
          return jsonify({'return':'Please give a rating between 0 to 5'})
     else:
          sturate = Rating.query.filter_by(tutID = request.json['tutID'], stuID = request.json['stuID']).first()
          if sturate:
               oldrate = sturate.ratings
               sturate.reviews = newrate.reviews
               sturate.ratings = newrate.ratings
               
               Rating.query.filter_by(tutID = request.json['tutID'], stuID = request.json['stuID']).update(dict(reviews=sturate.reviews))
               Rating.query.filter_by(tutID = request.json['tutID'], stuID = request.json['stuID']).update(dict(ratings=sturate.ratings))
               
               newCount = tutor.ratingCount
               newAvg = (tutor.avgRatings * tutor.ratingCount + float(newrate.ratings) - oldrate) / newCount
               Tutor.query.filter_by(id = request.json['tutID']).update(dict(avgRatings=newAvg))
               
               db.session.commit()
               return jsonify({'return':'rating edited'})
          else:
               newCount = tutor.ratingCount + 1
               newAvg = (tutor.avgRatings * tutor.ratingCount + float(newrate.ratings)) / newCount
               Tutor.query.filter_by(id = request.json['tutID']).update(dict(ratingCount=newCount))
               Tutor.query.filter_by(id = request.json['tutID']).update(dict(avgRatings=newAvg))
               db.session.add(newrate)
               db.session.commit()
               return jsonify({'return':'rating added'})
@myApp.route('/server/addSubjects', methods=['POST'])
def makesubjects():
	#adding to tutor table here
	id = request.json['id']
	print id
	tutor = Tutor.query.filter_by(id = request.json['id']).first()
	if tutor is None:
		return jsonify({'return':'error'})
	else:
		loc = tutor.location
		eraseTutor = Tutor(id, int(loc),'',tutor.avgRatings,tutor.ratingCount)
		db.session.merge(eraseTutor)
		db.session.commit()
		#erase end
		allSubjects = ""
		i = 0
		for subject in request.json['subjects']:
			print subject["subject"]
			if (i == 0):
				allSubjects = subject["subject"]
				i = i + 1
			else:
				i = i+1
				allSubjects = allSubjects + "," + subject["subject"]
		myTutor = Tutor(id, loc, allSubjects, tutor.avgRatings, tutor.ratingCount)
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
         # print student.pwdhash
         # print student.pwdhash
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
	tutor1 = Tutor.query.filter(Tutor.id == request.json['id']).first()
	if tutor1 is None:
		return jsonify({'return':'noSuccess'})
	tutor1.location = request.json['location']
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
