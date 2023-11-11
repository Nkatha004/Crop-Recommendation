# Crop Recommendation System

## Project Description
This is my final year project.

Problem: climate change is a topic that has become the attention of many including global leaders, heads of states and global organizations like the United Nations. Farmers in Kenya have been largely affected as they rely majorly on weather to grow crops. It has however become difficult to decide which crop to grow due to the changes in climate change which has created uncertainty in the weather.

Suppose there was a way to determine what crop would do best in your farm given the weather conditions and the soil on your farm?

The main objective of this project, as such, is to develop a machine learning model that can recommend to farmers the most suitable crop to grow based on the prevailing weather conditions and soil parameters.

Once the model is developed, the farmer needs a way of interacting with this model. How?🤔 This will be done through a very friendly interface developed using kotlin and android studio (A mobile app😃!)

Guess what? This project aligns with SDG goals to promote sustainable development. SDG GOAL 2: ZERO HUNGER


The code is above! HAVE FUN!😁

## So, What's the plan?
1. Train a machine learning model using google colab. (Partially) - Tweak hyperparameters
2. Export and download the model to use while creating an API.✅ This is in model training/model.pkl
2. Create an API using Flask to deploy the trained model.✅ crop_recommendation.py file has this code
3. Test the created API using Postman.✅
4. Deploy the Flask API - vercel is a good place to start✅
   `https://crop-recommendation-blond.vercel.app/predict`
6. Create a usable interface - mobile app to interact with the trained model.✅
