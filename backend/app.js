const express = require("express");
const app = express();
const { google } = require('googleapis');

require("dotenv").config();

const oauth2Client = new google.auth.OAuth2(
  process.env.GOOGLE_OAUTH_CLIENT_ID,
  process.env.GOOGLE_OAUTH_CLIENT_SECRET,
  process.env.GOOGLE_OAUTH_REDIRECT_URI,
);

app.get("/", (req, res) => {
  res.send("Hello World!");
});

app.get("/authorize", async (req, res) => {
  if (req.query.error || typeof req.query.code === "undefined") {
    return res.json({ error: "The code you provided is invalid." });
  }

  // Get access token
  const { tokens } = await oauth2Client.getToken(req.query.code);
  return res.json({ tokens })
});

const port = process.env.PORT || 3000;
app.listen(port, () => {
  console.log(`Example app listening on port ${port}`);
});
