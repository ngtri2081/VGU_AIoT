import cv2
from keras.models import load_model
import numpy as np
import tensorflow as tf
from PIL import Image, ImageOps

# Static variables
model_path = "MQTT Instructions\keras_model.h5"
label_path = "MQTT Instructions\labels.txt"

# Load the model
model = load_model(model_path)
cam = cv2.VideoCapture(0)

def image_capture():
    ret, frame = cam.read()
    cv2.imwrite("abc.png", frame)

def image_detector():
    # Create the array of the right shape to feed into the keras model
    # The 'length' or number of images you can put into the array is
    # determined by the first position in the shape tuple, in this case 1.
    data = np.ndarray(shape=(1, 224, 224, 3), dtype=np.float32)

    # Replace this with the path to your image
    image = Image.open('abc.png')

    #resize the image to a 224x224 with the same strategy as in TM2:
    #resizing the image to be at least 224x224 and then cropping from the center
    size = (224, 224)
    image = ImageOps.fit(image, size, Image.ANTIALIAS)

    #turn the image into a numpy array
    image_array = np.asarray(image)

    # Normalize the image
    normalized_image_array = (image_array.astype(np.float32) / 127.0) - 1

    # Load the image into the array
    data[0] = normalized_image_array

    # Run the inference
    prediction = model.predict(data)
    print(prediction)

    # Get the class that the model predicts
    max_index = int(tf.math.argmax(prediction[0]))

    file = open(label_path, encoding="utf8")
    data = file.read().split("\n")
    print("AI Result: ", data[max_index])
    return data[max_index]