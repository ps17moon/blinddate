# Flask + DeepFace API 예시 (Python)
from flask import Flask, request, jsonify
from deepface import DeepFace
import base64
import cv2
import numpy as np
import os

app = Flask(__name__)

def decode_base64_image(data):
    img_data = base64.b64decode(data)
    np_arr = np.frombuffer(img_data, np.uint8)
    return cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

@app.route('/verify', methods=['POST'])
def verify_faces():
    data = request.json
    face1 = decode_base64_image(data['face1'])
    face2 = decode_base64_image(data['face2'])

    try:
        result = DeepFace.verify(face1, face2, enforce_detection=False)
        return jsonify(result=result["verified"])
    except Exception as e:
        return jsonify(result=False)


if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))
    app.run(host='0.0.0.0', port=port)