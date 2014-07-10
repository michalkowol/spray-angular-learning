module.exports = function (config) {
    config.set({

        basePath: '../',

        files: [
//            'dist/bower_components/angular/angular.js',
//            'dist/bower_components/angular-mocks/angular-mocks.js',
//            'dist/bower_components/angular-loader/angular-loader.js',
//            'dist/js/**/*.js',
//            'test/unit/**/*.js'
        ],

        autoWatch: true,

        frameworks: ['mocha', 'chai', 'sinon-chai'],

        browsers: ['Chrome'],

        plugins: [
            'karma-chrome-launcher',
            'karma-mocha',
            'karma-chai-plugins',
            'karma-junit-reporter'
        ],

        junitReporter: {
            outputFile: 'test_out/unit.xml',
            suite: 'unit'
        }
    });
};
