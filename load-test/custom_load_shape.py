from locust.shape import LoadTestShape
from locustfile import GoldStandardUser


class StagesShapeWithCustomUsers(LoadTestShape):
    use_common_options = True
    stages = [
        {"duration": 120, "users":50, "spawn_rate": 10, "user_classes": [GoldStandardUser]}
    ]

    def tick(self):
        run_time = self.get_run_time()
        for stage in self.stages:
            if run_time < stage["duration"]:
                tick_data = (stage["users"], stage["spawn_rate"], stage["user_classes"])
                return tick_data
        return None