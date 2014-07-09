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
        .pipe(less())
        .pipe(gulp.dest('./target/'))
        .pipe(connect.reload());
});

gulp.task('jade', function () {
    return gulp.src('./app/**/*.jade')
        .pipe(jade({pretty: true}))
        .pipe(gulp.dest('./target/'))
        .pipe(connect.reload());
});

gulp.task('coffee', function () {
    return gulp.src('./app/**/*.coffee')
        .pipe(coffee({bare: true}).on('error', gutil.log))
        .pipe(gulp.dest('./target/'))
        .pipe(connect.reload());
});

gulp.task('copy-js', function () {
    return gulp.src('./app/**/*.js')
        .pipe(gulp.dest('./target/'))
        .pipe(connect.reload());
});

gulp.task('copy-css', function () {
    return gulp.src('./app/**/*.css')
        .pipe(gulp.dest('./target/'))
        .pipe(connect.reload());
});

gulp.task('copy-html', function () {
    return gulp.src('./app/**/*.html')
        .pipe(gulp.dest('./target/'))
        .pipe(connect.reload());
});

gulp.task('copy-src', ['copy-js', 'copy-css', 'copy-html']);

gulp.task('copy-assets', function () {
    return gulp.src('./app/**')
        .pipe(ignore.exclude(['**/*.js', '**/*.css', '**/*.html', '**/*.jade', '**/*.less', '**/*.coffee', '**/bower_components/**']))
        .pipe(gulp.dest('./target/'))
        .pipe(gulp.dest('./dist/'));
});

gulp.task('usemin', function () {
    return gulp.src('./target/**/*.html')
        .pipe(usemin({
            css: [minifycss(), 'concat', prefix('last 2 versions')],
            js: [ngmin({dynamic: true}), uglify(), rev()]
        }))
        .pipe(gulp.dest('./dist/'));
});

gulp.task('clean-target', function () {
    return gulp.src(['./target/'], {read: false})
        .pipe(clean());
});

gulp.task('clean-dist', function () {
    return gulp.src(['./dist/'], {read: false})
        .pipe(clean());
});

gulp.task('clean', ['clean-target', 'clean-dist']);

gulp.task('karma-unit', function () {
    return gulp.src('./test/unit/**.js')
        .pipe(karma({
            configFile: './test/test/karma.conf.js',
            action: 'run'
        }))
        .on('error', function (err) {
            throw err;
        });
});

gulp.task('karma-e2e', function () {
    return gulp.src('./test/e2e/**.js')
        .pipe(karma({
            configFile: './test/test/karma.conf.js',
            action: 'run'
        }))
        .on('error', function (err) {
            throw err;
        });
});

gulp.task('connect', function () {
    return connect.server({
        root: 'target',
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

gulp.task('watch', function () {
    gulp.watch(['./app/**/*.less'], ['less']);
    gulp.watch(['./app/**/*.jade'], ['jade']);
    gulp.watch(['./app/**/*.coffee'], ['coffee']);
    gulp.watch(['./app/**/*.js'], ['copy-js']);
    gulp.watch(['./app/**/*.css'], ['copy-css']);
    gulp.watch(['./app/**/*.html'], ['copy-html']);
});

gulp.task('build', function (callback) {
    runSequence('prettify', 'jshint', ['copy-src', 'copy-assets', 'less', 'jade', 'coffee'], callback);
});

gulp.task('dist', function (callback) {
    runSequence('clean', 'build', 'usemin', callback);
});

gulp.task('default', function (callback) {
    runSequence('build', ['connect', 'watch'], callback);
});