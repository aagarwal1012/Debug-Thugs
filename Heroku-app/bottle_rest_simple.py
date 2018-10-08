from bottle import run, get, post, request, delete, default_app
import model

# @get('/location')
# def getAll():
# 	#insert_db()
# 	return {'location' : database.myrecords()}

# 	#database.clear()

# @get('/clear/delete/db')
# def clear_DB():
# 	database.clear()



@post('/data')
def process():	
	#model.show(request.json)
	model.predictPotholes(request.json)
	#print(type(request.json))
	#return str

app = default_app()


#following for normal bottle app
#run(host = '0.0.0.0', reloader=True, server = 'gunicorn', debug=True, workers=4)

# to run a post request use following
# r = requests.post('http://localhost:8080/animal', json = dt)
