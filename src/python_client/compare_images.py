import base64
from os import path
from sys import argv
from time import sleep
import uuid

import json
from PIL import Image
import redis

MAX_REDIS_RETRIES = 10

r = redis.Redis(host="35.229.116.17", port=6379)

def image_to_base64(img_path):
    with open(img_path, "rb") as image_file:
        encoded_string = base64.b64encode(image_file.read())
        return encoded_string

def validate_and_order_images(path1, path2):
    if not (path.isfile(path1) and path.isfile(path2)):
        raise Exception("Files does not exist")

    im1 = Image.open(path1)
    width1, height1 = im1.size
    im2 = Image.open(path2)
    width2, height2 = im2.size
    # The image is not of the same dimensions
    if (height2>height1 and width2<width1) or (width2>width1 and height2<height1):
        return False, path1, path2

    if height2>=height1:
        return True, path2, path1
    else:
        return True, path1, path2

def images_to_redis(path1, path2):
    valid, path1, path2 = validate_and_order_images(path1, path2)
    print "Image to look in: {}, subimage {}".format(path1, path2)
    if not valid:
        raise Exception("Images doesnt have the same dimensions")

    request_id = uuid.uuid1().get_hex()
    img1 = image_to_base64(path1)
    img2 = image_to_base64(path2)
    msg = "{},{},{}".format(img1, img2, request_id)
    try:
        r.rpush("input", msg)
    except Exception as e:
        raise Exception("Couldn't send request to redis server")
    return request_id


def _print_result(r):
    data = json.loads(r)
    if data.get("match") == "f":
        print "There was not match for the given images. Calculated score was {score}".format(**data)
    else:
        print "There is a match for the given images, calculated score is {score} , positions are x:{x} y: {y}".format(**data)

if __name__ == "__main__":
    assert len(argv)==3
    request_id  = images_to_redis(argv[1], argv[2])
    retries = 0
    while retries <= MAX_REDIS_RETRIES:
        result = r.get(request_id)
        if result:
            _print_result(result)
            break
        retries += 1
        sleep(1)
    if retries == MAX_REDIS_RETRIES:
        print "Can retrieve data for request id '{}' from redis".format(request_id)
