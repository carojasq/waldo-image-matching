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

The python client is implemented in python2. There is a requirements file to install the libraries with pip.

`pip install -r src/python_client/requirements.txt`

### Usage

`python src/python_client/compare_images.py img1.jpg img2.jpg`

### Running the examples
```
python compare_images.py ../test/images/example1_complete.jpg ../test/images/example1_redcar.jpg
Image to look in: ../test/images/example1_complete.jpg, subimage ../test/images/example1_redcar.jpg
There is a match for the given images, calculated score is 0.7615725857904181 , positions are x:650.0 y: 277.0
```

```
 python compare_images.py ../test/images/example1_complete.jpg ../test/images/example1_redcar_no_match.jpg 
Image to look in: ../test/images/example1_complete.jpg, subimage ../test/images/example1_redcar_no_match.jpg
There was not match for the given images. Calculated score was 0.20054876804351807
```

```
 python compare_images.py ../test/images/example1_complete.jpg ../test/images/example1_redcar80.jpg 
Image to look in: ../test/images/example1_complete.jpg, subimage ../test/images/example1_redcar80.jpg
There is a match for the given images, calculated score is 0.7560648082289845 , positions are x:650.0 y: 277.0
```
