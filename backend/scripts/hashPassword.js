const bcrypt = require('bcryptjs');

(async () => {
  const hashedPassword = await bcrypt.hash('JorgeC5173380', 10);
  console.log('Hash bcrypt:', hashedPassword);
})();
