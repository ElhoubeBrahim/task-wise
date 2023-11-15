const fs = require("fs");

const OpenAI = require('openai');
const openai = new OpenAI({
  apiKey: process.env.OPENAI_API_KEY,
});

const setupPrompt = (todo) => {
  // Build the prompt
  let prompt = fs.readFileSync("./prompt.txt", "utf8");
  prompt = prompt + "\n\n" + todo.join("\n");

  // Add date
  prompt = prompt.replaceAll("{{date}}", new Date().toISOString().slice(0, 10));

  return prompt;
}

const getPromptCompletion = async (prompt) => {
  const completions = await openai.chat.completions.create({
    messages: [{ role: "user", content: prompt }],
    model: "gpt-3.5-turbo",
  });

  return completions.choices ? completions.choices[0].message.content : "";
}

const verifyGoogleAccessToken = async (accessToken) => {
  const url = `https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=${accessToken}`;
  const response = await fetch(url);

  return response.status === 200;
}

module.exports = { setupPrompt, getPromptCompletion, verifyGoogleAccessToken };