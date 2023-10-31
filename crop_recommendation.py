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
            'avocado': 0,
            'banana': 1,
            'beans': 2,
            'blackgram': 3,
            'cabbage': 4,
            'cassava': 5,
            'coffee': 6,
            'groundnut': 7,
            'lentil(kamande)': 8,
            'maize': 9,
            'mango': 10,
            'millet': 11,
            'orange': 12,
            'pawpaw': 13,
            'pigeonpeas(mbaazi)': 14,
            'potato': 15,
            'rice': 16,
            'sorghum': 17,
            'tea': 18,
            'tomato': 19,
            'watermelon': 20,
            'wheat': 21
        }

        result = list(labels_mappings.keys())[list(labels_mappings.values()).index(prediction[0])]

    return jsonify({"result": result})


if __name__ == '__main__':
    app.run(debug=True)