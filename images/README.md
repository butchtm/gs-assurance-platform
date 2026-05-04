# Gold Standard Assurance Images

Provides the images used for the build and deployment processes for the Gold Standard Assurance services

# Images Provided

This repository currently provides the facility to build the following images via GitLab pipeline schedules:

- Pipeline image for the Assurance back-end API
- Base image for the Assurance back-end API

# Building Images

To build images, navigate to the Build -> Pipeline Schedules section within GitLab and click Run on the desired job.

# Adding New Images

To add new images, add a build job into .gitlab-ci.yml as well as a scheduled job within Build -> Pipeline Schedules in GitLab.