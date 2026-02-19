'use strict';

module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.addColumn('Users', 'services', { type: Sequelize.JSON });
    await queryInterface.addColumn('Users', 'cities', { type: Sequelize.JSON });
    await queryInterface.addColumn('Users', 'idFrontUrl', { type: Sequelize.STRING });
    await queryInterface.addColumn('Users', 'idBackUrl', { type: Sequelize.STRING });
    await queryInterface.addColumn('Users', 'certificates', { type: Sequelize.JSON });
  },

  async down(queryInterface, Sequelize) {
    await queryInterface.removeColumn('Users', 'services');
    await queryInterface.removeColumn('Users', 'cities');
    await queryInterface.removeColumn('Users', 'idFrontUrl');
    await queryInterface.removeColumn('Users', 'idBackUrl');
    await queryInterface.removeColumn('Users', 'certificates');
  }
};
