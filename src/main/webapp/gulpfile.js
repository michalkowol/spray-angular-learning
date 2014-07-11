(function () {
    'use strict';

    var gulp = require('gulp');
    var gutil = require('gulp-util');
    var clean = require('gulp-clean');
    var uglify = require('gulp-uglify');
    var minifycss = require('gulp-minify-css');
    var connect = require('gulp-connect');
    var jshint = require('gulp-jshint');
    var ignore = require('gulp-ignore');
    var usemin = require('gulp-usemin');
    var rev = require('gulp-rev');
    var prefix = require('gulp-autoprefixer');
    var less = require('gulp-less');
    var jade = require('gulp-jade');
    var ngmin = require('gulp-ngmin');
    var prettify = require('gulp-jsbeautifier');
    var runSequence = require('run-sequence');
    var karma = require('gulp-karma');
    var coffee = require('gulp-coffee');
    var typescript = require('gulp-tsc');

    gulp.task('jshint', function () {
        return gulp.src('./app/js/**/*.js')
            .pipe(jshint())
            .pipe(jshint.reporter('jshint-stylish'));
    });

    gulp.task('prettify', function () {
        return gulp.src(['./app/**/*.js', './app/**/*.css', './app/**/*.html'])
            .pipe(ignore.exclude(['**/bower_components/**']))
            .pipe(prettify({config: '.jsbeautifyrc', mode: 'VERIFY_AND_WRITE'}))
            .pipe(gulp.dest('./app/'));
    });

    gulp.task('less', function () {
        return gulp.src('./app/**/*.less')
            .pipe(ignore.exclude('**/bower_components/**'))
            .pipe(less())
            .pipe(gulp.dest('./dist/'))
            .pipe(connect.reload());
    });

    gulp.task('jade', function () {
        return gulp.src('./app/**/*.jade')
            .pipe(jade({pretty: true}))
            .pipe(gulp.dest('./dist/'))
            .pipe(connect.reload());
    });

    gulp.task('coffee', function () {
        return gulp.src('./app/**/*.coffee')
            .pipe(coffee({bare: true}).on('error', gutil.log))
            .pipe(gulp.dest('./dist/'))
            .pipe(connect.reload());
    });

    gulp.task('typescript', function () {
        return gulp.src('./app/**/*.ts')
            .pipe(typescript(typescript({sourcemap: true, outDir: './dist/'})))
            .pipe(gulp.dest('./dist/'))
            .pipe(connect.reload());
    });

    gulp.task('js', function () {
        return gulp.src('./app/**/*.js')
            .pipe(gulp.dest('./dist/'))
            .pipe(connect.reload());
    });

    gulp.task('css', function () {
        return gulp.src('./app/**/*.css')
            .pipe(gulp.dest('./dist/'))
            .pipe(connect.reload());
    });

    gulp.task('html', function () {
        return gulp.src('./app/**/*.html')
            .pipe(gulp.dest('./dist/'))
            .pipe(connect.reload());
    });

    gulp.task('src', ['js', 'css', 'html']);

    gulp.task('copy-assets', function () {
        return gulp.src('./app/**')
            .pipe(ignore.exclude(['**/*.js', '**/*.css', '**/*.html', '**/*.jade', '**/*.less', '**/*.coffee', '**/*.ts', '**/bower_components/**']))
            .pipe(gulp.dest('./dist/'));
    });

    gulp.task('usemin', function () {
        return gulp.src('./dist/**/*.html')
            .pipe(usemin({
                css: [minifycss(), 'concat', prefix('last 2 versions'), rev()],
                js: [ngmin({dynamic: true}), uglify(), rev()]
            }))
            .pipe(gulp.dest('./dist/'));
    });

    gulp.task('clean', function () {
        return gulp.src(['./dist/'], {read: false})
            .pipe(clean());
    });

    var unitTestFiles = [
        './dist/bower_components/angular/angular.js',
        './dist/bower_components/angular-mocks/angular-mocks.js',
        './dist/bower_components/angular-loader/angular-loader.js',
        './dist/js/**/*.js',
        './test/unit/**/*.js'
    ];

    var disUnitTestFiles = [
        './dist/bower_components/angular/angular.js',
        './dist/bower_components/angular-mocks/angular-mocks.js',
        './dist/bower_components/angular-loader/angular-loader.js',
        './dist/js/js/head-script*.js',
        './test/unit/**/*.js'
    ];

    var e2eTestFiles = [
        './dist/bower_components/angular/angular.js',
        './dist/bower_components/angular-mocks/angular-mocks.js',
        './dist/bower_components/angular-loader/angular-loader.js',
        './dist/js/**/*.js',
        './test/e2e/**/*.js'
    ];

    gulp.task('karma-dist-unit', function () {
        return gulp.src(disUnitTestFiles)
            .pipe(karma({
                configFile: './test/karma.conf.js',
                action: 'run'
            }))
            .on('error', function (err) {
                throw err;
            });
    });

    gulp.task('karma-tdd-unit', function () {
        return gulp.src(unitTestFiles)
            .pipe(karma({
                configFile: './test/karma.conf.js',
                action: 'watch'
            }));
    });

    gulp.task('karma-e2e', function () {
        return gulp.src(e2eTestFiles)
            .pipe(karma({
                configFile: './test/karma.conf.js',
                action: 'run'
            }))
            .on('error', function (err) {
                throw err;
            });
    });

    gulp.task('connect', function () {
        return connect.server({
            root: 'dist',
            port: 8081,
            livereload: true,
            middleware: function () {
                return [ (function () {
                    var url = require('url');
                    var proxy = require('proxy-middleware');
                    var options = url.parse('http://localhost:8080/api');
                    options.route = '/api';
                    return proxy(options);
                })() ];
            }
        });
    });

    gulp.task('js-with-jshint', function (callback) {
        runSequence('jshint', 'js', callback);
    });

    gulp.task('watch', function () {
        gulp.watch(['./app/**/*.less'], ['less']);
        gulp.watch(['./app/**/*.jade'], ['jade']);
        gulp.watch(['./app/**/*.coffee'], ['coffee']);
        gulp.watch(['./app/**/*.ts'], ['typescript']);
        gulp.watch(['./app/**/*.js'], ['js-with-jshint']);
        gulp.watch(['./app/**/*.css'], ['css']);
        gulp.watch(['./app/**/*.html'], ['html']);
    });

    gulp.task('build', function (callback) {
        runSequence('prettify', 'jshint', ['src', 'copy-assets', 'less', 'jade', 'coffee', 'typescript'], callback);
    });

    gulp.task('dist', function (callback) {
        runSequence('clean', 'build', 'usemin', 'karma-dist-unit', callback);
    });

    gulp.task('server', function (callback) {
        runSequence('build', ['connect', 'watch', 'karma-tdd-unit'], callback);
    });

    gulp.task('default', ['dist']);
})();