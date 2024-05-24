# Cooking Recipe App

Recipe management app powered by Kotlin, developed for a University project.

Hosted for archiving purposes, not functional without a backend with neccesary API endpoints.

## Usage

- Download Android Studio and open a new project using this repository url
- Run the backend instance either using Docker or your local IDE debugger
- Configure the RetrofitProvider file to update the API adress url:
    + Open up a terminal and run "ipconfig" (ifconfig in unix) to find out your IP adress
    + Paste your api adress and the port of the backend (8080 in our case) in the apiUrl variable
- Disable your firewall or add an exception to ensure the connection between server and client will establish
- Configure an emulator or connect your own android device and run the project