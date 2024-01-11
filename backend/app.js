require("dotenv").config();

const express = require("express");
const app = express();
const { google } = require("googleapis");
const path = require("path");
const { setupPrompt, getPromptCompletion, verifyGoogleAccessToken } = require("./helpers");
const logger = require("./logger");

// Setup json body parser
app.use(express.json());

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

app.post("/events", async (req, res) => {
  // Get user ip address
  const ip_address = req.ip;

  // Get user todo list
  let todo = req.body.todo;
  if (!todo || todo.filter((item) => item !== "").length === 0) {
    logger.error(ip_address, null, "Todo list is empty.");
    return res.status(400).json({ error: "You must provide a todo list." });
  }

  todo = todo
    .filter((item) => item !== "")
    .map((item) => item.trim());

  // Verify access token
  const accessToken = req.headers.authorization?.split(" ")[1] || null;
  const isAccessTokenValid = accessToken && await verifyGoogleAccessToken(accessToken);
  if (!isAccessTokenValid) {
    logger.error(ip_address, JSON.stringify(todo), "Invalid access token.");
    return res.status(401).json({ error: "You must provide a valid access token." });
  }

  // Build the prompt
  let prompt = setupPrompt(todo);

  // Use OpenAI api to generate events
  let response = await getPromptCompletion(prompt);

  // Check if response is a parseable json
  try {
    response = JSON.parse(response);
  } catch (e) {
    logger.error(ip_address, JSON.stringify(todo), e.message);
    return res.status(500).json({ error: "Something went wrong. Please try again." });
  }

  logger.success(ip_address, JSON.stringify(todo), JSON.stringify(response));
  res.json({ ...response });
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
  logger.setupDatabase();
  console.log(`Example app listening on port ${port}`);
});
