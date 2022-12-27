from shapely.geometry import Point
from shapely.geometry.polygon import Polygon
import cv2
import datetime
import threading
import numpy as np

class YOLO():
    def __init__(self, detect_class, client, frame_width=720, frame_height=720):
        # Parameters
        self.classnames_file = "Model/classnames.txt"
        self.weights_file = "Model/yolov7-tiny.weights"
        self.config_file = "Model/yolov7-tiny.cfg"
        self.model = cv2.dnn.readNet(self.weights_file, self.config_file)
        self.detect_class = detect_class
        self.frame_width = frame_width
        self.frame_height = frame_height
        self.conf_threshold = 0.5
        self.nms_threshold = 0.4
        self.scale = 1 / 255
        self.classes = None
        self.output_layers = None
        self.last_alert = None
        self.time_threshold = 5  # WARNING: Do not set this value too low
        self.client = client
        self.read_class_file()
        self.get_output_layers()

    def read_class_file(self):
        with open(self.classnames_file, 'r') as f:
            # Read all the class names into a list
            self.classes = [line.strip() for line in f.readlines()]

    def get_output_layers(self):
        layer_names = self.model.getLayerNames()
        self.output_layers = [layer_names[i - 1] for i in self.model.getUnconnectedOutLayers()]

    def draw_prediction(self, img, class_id, x, y, x_plus_w, y_plus_h, points):
        label = str(self.classes[class_id])
        # Color for the bounding box
        color = (0, 255, 0)
        cv2.rectangle(img, (x, y), (x_plus_w, y_plus_h), color, 2)
        # Display label at the top of the bounding box
        cv2.putText(img, label, (x - 10, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, color, 2)
        # Calculate centroid of the bounding box
        centroid = ((x + x_plus_w) // 2, (y + y_plus_h) // 2)
        cv2.circle(img, centroid, 5, (color), -1)

        polygon = Polygon(points)
        centroid = Point(centroid)
        # Check if centroid is inside the polygon
        isInside = polygon.contains(centroid)
        if isInside:
            # Alert if centroid is inside the polygon
            img = self.alert(img)

        return isInside

    def alert(self, img):
        cv2.putText(img, "ALARM!!!!", (10, 50), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2)
        if (self.last_alert is None) or (
                (datetime.datetime.utcnow() - self.last_alert).total_seconds() > self.time_threshold):
            self.last_alert = datetime.datetime.utcnow()
            thread = threading.Thread(target=self.client.publish("intrusion-detector", "WARNING!"))
            thread.start()
        return img

    def detect(self, frame, points):
        blob = cv2.dnn.blobFromImage(frame, self.scale, (416, 416), (0, 0, 0), True, crop=False)
        self.model.setInput(blob)
        outs = self.model.forward(self.output_layers)

        # Detecting objects
        class_ids = []
        confidences = []
        boxes = []

        for out in outs:
            for detection in out:
                scores = detection[5:]
                class_id = np.argmax(scores)
                confidence = scores[class_id]
                # Object detected
                if (confidence >= self.conf_threshold) and (self.classes[class_id] in self.detect_class):
                    center_x = int(detection[0] * self.frame_width)
                    center_y = int(detection[1] * self.frame_height)
                    w = int(detection[2] * self.frame_width)
                    h = int(detection[3] * self.frame_height)
                    x = center_x - w / 2
                    y = center_y - h / 2
                    class_ids.append(class_id)
                    confidences.append(float(confidence))
                    boxes.append([x, y, w, h])
        # Apply non-max suppression
        indices = cv2.dnn.NMSBoxes(boxes, confidences, self.conf_threshold, self.nms_threshold)

        for i in indices:
            box = boxes[i]
            x = box[0]
            y = box[1]
            w = box[2]
            h = box[3]
            # Draw the predicted bounding box
            self.draw_prediction(frame, class_ids[i], round(x), round(y), round(x + w), round(y + h), points)

        return frame
