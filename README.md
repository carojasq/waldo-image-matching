[![Build Status](https://travis-ci.org/julianovidal/storm-redis.svg?branch=master)](https://travis-ci.org/julianovidal/storm-redis)

# Image Matching Storm
Implementation of storm topology for template matching operations using opencv.

This project uses a storm redis spout implementation taken from : https://github.com/julianovidal/storm-redis 
## How it works
Both images converted to base64 and are send to redis with a request id.

The topology reads the base64 encoded images and performs the template matching writing the results to redis.

### Nimbus of the topology

http://35.229.116.17:8080/index.html

### Requirements
* Storm 
* libopencv
* Redis

### Running the topology 
`storm jar image-matching-storm-1.0.0.jar storm.ImageMatchingTopology`

## Python Client
### Requirements

python2 should be installed in client machine.

`pip install -r src/python_client/requirements.txt`

### Usage

`python src/python_client/compare_images.py img1.jpg img2.jpg`
