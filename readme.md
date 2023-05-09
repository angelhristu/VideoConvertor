# Video Conversion API

This is a simple API that allows users to convert a video file into different formats. The API is built using Spring Boot, and leverages the FFmpeg tool to perform the video conversion.

## Table of Contents

- [Requirements](#requirements)
- [Configuration](#configuration)
- [Usage](#usage)

## Requirements

- JDK 17 
- Gradle
- FFmpeg installed and available in the PATH
- On a CI/CD pipeline, the VMs running the build should have FFmpeg installed


## Configuration

The API requires the path to the input video file to be set in the `application.properties` file. At runtime you can upload a video in the resources folder and then update the `converted-video.path` property with the path to the input video file:

```properties
converted-video.path=/path/to/your/input/video/file
```

## Usage

To convert a video, make a GET request to the `/api/convert` endpoint with the desired output format specified in the `Accept` header:

```bash
curl -H "Accept: mkv" http://localhost:8080/api/convert --output output.mkv
```

