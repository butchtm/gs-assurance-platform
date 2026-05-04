# GoldStandard Load Tester

## Setup
### Requirements
* Python
* PyCharm (for development)

### Virtual environment
1. Create virtual environment:
   * Windows: `py -m venv venv`
   * Linux/MacOS: `python3 -m venv venv`
2. Activate it:
   * Windows: `.\venv\Scripts\activate`
   * Linux/MacOS: `source venv/bin/activate`
3. Install dependencies: `pip install -r requirements.txt`

### Write environment config
1. Create a `.env` file in the project root
2. Populate it with the required auth variables (see example file)

## Running the app
1. Ensure your Virtual Environment is activated
2. Run the command: `locust`
3. Navigate to [localhost:8089](http://localhost:8089) to view the results

## Run Options
### Master - Worker
1. Configure the `expect-workers` value in the `locust.conf` file for the number of workers to expect.
2. Run the command: `locust --master` on one terminal.
3. Run the command: `locust --worker` on a second terminal.  

### Custom Load Shapes
1. Configure the stages values in the `custom_load_shape.py` file to create the test you'd like to run, including: duration, users and spawn rate.
2. Run the command: `locust -f locustfile.py, custom_load_shape.py`