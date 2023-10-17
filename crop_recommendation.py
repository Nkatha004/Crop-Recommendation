from flask import Flask,request,jsonify
import joblib
import warnings

app = Flask(__name__)

warnings.filterwarnings('ignore')
classifier = joblib.load("model training/model.pkl")

@app.route('/')
def index():
    return "Hello world"


@app.route('/predict',methods=['POST'])
def predict():
    if request.method == 'POST':
        nitrogen = float(request.form['nitrogen'])
        potassium = float(request.form['potassium'])
        phosphorous = float(request.form['phosphorous'])
        rainfall = float(request.form['rainfall'])
        temperature = float(request.form['temperature'])
        humidity = float(request.form['humidity'])
        ph = float(request.form['ph'])
        
        prediction = classifier.predict([
            [nitrogen, phosphorous, potassium, temperature, humidity, ph, rainfall]
        ])

        labels_mappings = {
            'apple': 0,
            'banana': 1,
            'blackgram': 2,
            'chickpea(mbaazi)': 3,
            'coconut': 4,
            'coffee': 5,
            'cotton': 6,
            'grapes': 7,
            'jute(mrenda)': 8,
            'kidneybeans': 9,
            'lentil': 10,
            'maize': 11,
            'mango': 12,
            'mothbeans': 13,
            'mungbean': 14,
            'muskmelon': 15,
            'orange': 16,
            'pawpaw': 17,
            'pigeonpeas': 18,
            'pomegranate': 19,
            'rice': 20,
            'watermelon': 21
        }

    return list(labels_mappings.keys())[list(labels_mappings.values()).index(prediction[0])]


if __name__ == '__main__':
    app.run(debug=True)