# Calendar Alarm Server

This server connects to a Google Calendar account and return calendar status based on the following events.

## Configuration

Create an application.properties file in the application directory with the following properties:

```properties
server.calendar-id=your-google-email
```

When run for the first time or after removing `StoredCredentials` from the user's home `.alarm-server/store/` directory, the server will ask for the user's consent to access the Google Calendar account.

## Development

- Use Java 17 or later.
- User the **Maven** wrapper to build the project.
- Use the `local` Spring profile and setup your test account in the `application-local.properties` file.