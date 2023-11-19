const express = require("express");
const app = express();
const { google } = require("googleapis");
const path = require("path");

require("dotenv").config();

const oauth2Client = new google.auth.OAuth2(
  process.env.GOOGLE_OAUTH_CLIENT_ID,
  process.env.GOOGLE_OAUTH_CLIENT_SECRET,
  process.env.GOOGLE_OAUTH_REDIRECT_URI,
);

app.use(express.static(path.join(__dirname, "public")));

app.set("views", path.join(__dirname, "views"));
app.set("view engine", "ejs");

app.get("/", (req, res) => {
  res.send("Hello World!");
});

app.get("/authorize", async (req, res) => {
  if (req.query.error || typeof req.query.code === "undefined") {
    return res.json({ error: "The code you provided is invalid." });
  }

  try {
    const { tokens } = await oauth2Client.getToken(req.query.code);
    const data = { access_token: tokens.access_token };

    res.render("response", { data });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: "An error occurred while fetching data." });
  }
});

const port = process.env.PORT || 3000;
app.listen(port, () => {
  console.log(`Example app listening on port ${port}`);
});
