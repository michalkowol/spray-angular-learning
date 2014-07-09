var gulp = require('gulp');
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

gulp.task('jshint', function () {
    gulp.src('./app/js/**/*.js')
        .pipe(jshint())
        .pipe(jshint.reporter('jshint-stylish'));
});

gulp.task('prettify', function () {
    gulp.src(['./app/**/*.js', './app/**/*.css', './app/**/*.html'])
        .pipe(ignore.exclude(['**/bower_components/**']))
        .pipe(prettify({config: '.jsbeautifyrc', mode: 'VERIFY_AND_WRITE'}))
        .pipe(gulp.dest('./app/'))
});

gulp.task('less', function () {
    gulp.src('./app/less/**/*.less')
        .pipe(less())
        .pipe(gulp.dest('./app/css/'));
});

gulp.task('jade', function () {
    gulp.src('./app/**/*.jade')
        .pipe(jade())
        .pipe(gulp.dest('./app/'))
});

gulp.task('usemin', function () {
    gulp.src('./app/**/*.html')
        .pipe(usemin({
            css: [minifycss(), 'concat', prefix('last 2 versions')],
            js: [ngmin({dynamic: true}), uglify(), rev()]
        }))
        .pipe(gulp.dest('./dist/'));
});

gulp.task('copy', function () {
    gulp.src('./app/**')
        .pipe(ignore.exclude(['**/*.js', '**/*.css', '**/*.html', '**/*.jade', '**/less/**', '**/bower_components/**']))
        .pipe(gulp.dest('./dist/'));
});

gulp.task('clean', function () {
    gulp.src(['./dist/'], {read: false})
        .pipe(clean());
});

gulp.task('connect', function () {
    connect.server({
        root: 'app',
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

gulp.task('reload', function () {
    gulp.src('./app/**')
        .pipe(connect.reload());
});

gulp.task('watch', function () {
    gulp.watch(['./app/less/**/*.less'], ['less']);
    gulp.watch(['./app/**/*.jade'], ['jade']);
    gulp.watch(['./app/js/**/*.js'], ['jshint', 'reload']);
    gulp.watch(['./app/css/**/*.css'], ['reload']);
    gulp.watch(['./app/**/*.html'], ['reload']);
});

gulp.task('build', ['jshint', 'less', 'jade']);
gulp.task('dist', ['build', 'usemin', 'copy']);
gulp.task('default', ['build', 'connect', 'watch']);