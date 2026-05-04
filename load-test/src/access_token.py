import requests


class AuthenticationCookiesGenerator:
    def get_token(self, auth_host: str, username: str, password: str):
        token_response = requests.post(
            f"{auth_host}/api/public/login",
            json={'username': username, 'password': password}
        )

        if not token_response.ok:
            print(f"Error getting token ({token_response.reason}):\n{token_response.json()}")
            token_response.raise_for_status()
            exit()

        response = token_response.cookies.get_dict()
        return response