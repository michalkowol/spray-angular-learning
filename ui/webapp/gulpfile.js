(function () {
    'use strict';

    var gulp = require('gulp');
    var jade = require('gulp-jade');
    var usemin = require('gulp-usemin');
    var jshint = require('gulp-jshint');
    var browserify = require('gulp-browserify');
    var stylus = require('gulp-stylus');
    var nib = require('nib');
    var prefix = require('gulp-autoprefixer');
    var uglify = require('gulp-uglify');
    var minifycss = require('gulp-minify-css');
    var connect = require('gulp-connect');
    var runSequence = require('run-sequence');
    var del = require('del');

    gulp.task('jade', function () {
        return gulp.src('app/**/*.jade')
            .pipe(jade({pretty: true}))
            .pipe(gulp.dest('dist'))
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
            .pipe(gulp.dest('dist/js'))
            .pipe(connect.reload());
    });

    gulp.task('js', ['jshint'], function () { // browserify
        return gulp.src('app/**/*.js')
            .pipe(gulp.dest('dist'))
            .pipe(connect.reload());
    });

    gulp.task('css', function () {
        return gulp.src('app/**/*.css')
            .pipe(gulp.dest('dist'))
            .pipe(connect.reload());
    });

    gulp.task('stylus', function () {
        return gulp.src('app/css/**/*.styl')
            .pipe(stylus({
                use: [nib()],
                sourcemap: {
                    inline: true,
                    sourceRoot: '..',
                    basePath: 'css'
                }
            }))
            .pipe(gulp.dest('dist/css'))
            .pipe(connect.reload());
    });

    gulp.task('img', function () {
        return gulp.src('app/img/**')
            .pipe(gulp.dest('dist/img'));
    });

    gulp.task('usemin', function () {
        return gulp.src('dist/**/*.html')
            .pipe(usemin({
                css: [minifycss(), 'concat', prefix()],
                js: [uglify()]
            }))
            .pipe(gulp.dest('dist'));
    });

    gulp.task('connect', function () {
        return connect.server({
            root: 'dist',
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
        del(['dist/bower_components'], callback)
    });

    gulp.task('watch', function () {
        gulp.watch(['app/**/*.jade'], ['jade']);
        gulp.watch(['app/**/*.css'], ['css']);
        gulp.watch(['app/**/*.js'], ['js']);
        gulp.watch(['app/**/*.styl'], ['stylus']);
    });

    gulp.task('build', ['img', 'jade', 'js', 'css', 'stylus']);
    gulp.task('dist', function (callback) {
        runSequence('clean', 'build', 'usemin', 'clean-bower', callback);
    });
    gulp.task('server', ['build', 'connect', 'watch']);
    gulp.task('default', ['dist']);
})();