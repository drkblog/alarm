# Calendar Alarm Server

This server connects to a Google Calendar account and return calendar status based on the following events.

## Configuration

Create an `application.properties` file in the application directory to provide your **Google calendar ID**.
Optionally, you can also provide a time window for the alarm to check for events. The default is set to 5 minutes:

```properties
alarm.calendar-id=your-google-email
alarm.time-window=PT3M
```

When run for the first time or after removing `StoredCredentials` from the user's home `.alarm-server/store/` directory, the server will ask for the user's consent to access the Google Calendar account.

## Development

- Use Java 17 or later.
- User the **Maven** wrapper to build the project.
- Use the `local` Spring profile and setup your test account in the `application-local.properties` file.

### Pending work

- Remove event window logic from `CalendarService` into a different class.
- Make the event window configurable.