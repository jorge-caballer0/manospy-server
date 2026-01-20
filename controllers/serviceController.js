const { ServiceRequest, User } = require('../models');

exports.getPendingRequests = async (req, res) => {
  try {
    const requests = await ServiceRequest.findAll({
      where: { status: 'pending' },
      include: [{ model: User, as: 'client', attributes: ['name'] }]
    });
    res.json(requests);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
};

exports.createRequest = async (req, res) => {
  try {
    const reqData = await ServiceRequest.create(req.body);
    res.json(reqData);
  } catch (e) {
    res.status(400).json({ error: e.message });
  }
};
