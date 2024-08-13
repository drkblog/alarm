# Calendar Alarm Server

This server connects to a Google Calendar account and return calendar status based on the following events.

## Configuration

You need to provide a Google API client secret with permissions to use the Calendar API.
- Create an API client in [Google Could Console](https://console.cloud.google.com/)
  - Enable the **Google Calendar API** for the project.
  - Create a new OAuth 2.0 client ID.
  - Set the application type to **Desktop app**.
  - Add at least one Google user in the OAuth consent screen section.
- Download the `credentials.json` file to the application `main/resources` directory.
- Rename the file to `alarm-server-client-secret.json`.

Create an `application.properties` file in the application directory to provide your **Google calendar ID**.
Optionally, you can also provide a time window for the alarm to check for events. The default is set to 5 minutes:

```properties
alarm.calendar-id=your-google-email
alarm.time-window=PT3M
```
If everything is set up correctly, when run for the first time or after removing `StoredCredentials` from the user's home `.alarm-server/store/` directory, the server will ask for the user's consent to access the **Google Calendar account**.
Once the user consents, the server will store the credentials in the `StoredCredentials` file.
And it will connect to the Google Calendar account to check for events when needed.

You can test if the server is working by running the following command:

```shell
curl http://localhost:2525/calendar/status
```

## Development

- Use Java 17 or later.
- User the **Maven** wrapper to build the project.
- Use the `local` Spring profile and setup your test account in the `application-local.properties` file.
