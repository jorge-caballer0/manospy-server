// For tests, set env before importing
process.env.DATABASE_URL = 'sqlite::memory:';

import request from 'supertest';
import express from 'express';
import bodyParser from 'body-parser';
import chatRoutes from '../routes/chatRoutes.js';

// Simple integration-like tests using in-memory express server
const app = express();
app.use(bodyParser.json());
app.use('/chats', chatRoutes);

describe('Chat routes (smoke)', () => {
  test('POST /chats returns 401 without auth', async () => {
    const res = await request(app).post('/chats').send({ offerId: 'offer1' });
    expect(res.statusCode).toBe(401);
  });
});
