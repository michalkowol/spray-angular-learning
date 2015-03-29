(function () {
    'use strict';

    var gulp = require('gulp');
    var react = require('gulp-react');
    var jade = require('gulp-jade');
    var usemin = require('gulp-usemin');
    var jshint = require('gulp-jshint');
    var browserify = require('gulp-browserify');
    var stylus = require('gulp-stylus');
    var nib = require('nib');
    var sourcemaps = require('gulp-sourcemaps');
    var prefix = require('gulp-autoprefixer');
    var uglify = require('gulp-uglify');
    var minifycss = require('gulp-minify-css');
    var connect = require('gulp-connect');
    var runSequence = require('run-sequence');
    var del = require('del');

    gulp.task('jade', function () {
        return gulp.src('app/**/*.jade')
            .pipe(jade({pretty: true}))
            .pipe(gulp.dest('dist/assets'))
            .pipe(connect.reload());
    });

    gulp.task('jshint', function () {
        return gulp.src('app/js/**/*.js')
            .pipe(jshint())
            .pipe(jshint.reporter('jshint-stylish'));
    });

    gulp.task('browserify', ['jshint'], function () {
        return gulp.src('app/js/app.js')
            .pipe(browserify({
                insertGlobals: true,
                debug: true
            }))
            .pipe(gulp.dest('dist/assets/js'))
            .pipe(connect.reload());
    });

    gulp.task('js', ['react', 'jshint'], function () { // browserify
        return gulp.src('app/**/*.js')
            .pipe(gulp.dest('dist/assets'))
            .pipe(connect.reload());
    });

    gulp.task('css', function () {
        return gulp.src('app/**/*.css')
            .pipe(gulp.dest('dist/assets'))
            .pipe(connect.reload());
    });

    gulp.task('stylus', function () {
        return gulp.src('app/css/**/*.styl')
            .pipe(sourcemaps.init())
            .pipe(stylus({use: [nib()]}))
            .pipe(sourcemaps.write())
            .pipe(gulp.dest('dist/assets/css'))
            .pipe(connect.reload());
    });

    gulp.task('react', function () {
        return gulp.src('app/**/*.jsx')
            .pipe(sourcemaps.init())
            .pipe(react())
            .pipe(sourcemaps.write())
            .pipe(gulp.dest('dist/assets'))
            .pipe(connect.reload());;
    });

    gulp.task('img', function () {
        return gulp.src('app/img/**')
            .pipe(gulp.dest('dist/assets/img'));
    });

    gulp.task('usemin', function () {
        return gulp.src('dist/assets/**/*.html')
            .pipe(usemin({
                css: [minifycss(), 'concat', prefix()],
                js: [uglify()]
            }))
            .pipe(gulp.dest('dist/assets'));
    });

    gulp.task('connect', function () {
        return connect.server({
            root: 'dist/assets',
            port: 8081,
            livereload: true,
            middleware: function () {
                return [ (function () {
                    var proxy = require('proxy-middleware');
                    return proxy({
                        port: 8080,
                        pathname: '/api',
                        route: '/api'
                    });
                })() ];
            }
        });
    });

    gulp.task('clean', function (callback) {
        del(['dist'], callback)
    });

    gulp.task('clean-bower', function (callback) {
        del(['dist/assets/bower_components'], callback)
    });

    gulp.task('watch', function () {
        gulp.watch(['app/**/*.jade'], ['jade']);
        gulp.watch(['app/**/*.css'], ['css']);
        gulp.watch(['app/**/*.js'], ['js']);
        gulp.watch(['app/**/*.styl'], ['stylus']);
        gulp.watch(['app/**/*.jsx'], ['react']);
    });

    gulp.task('build', ['img', 'jade', 'js', 'css', 'stylus']);
    gulp.task('dist', function (callback) {
        runSequence('clean', 'build', 'usemin', 'clean-bower', callback);
    });
    gulp.task('server', ['build', 'connect', 'watch']);
    gulp.task('default', ['dist']);
})();