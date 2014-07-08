var gulp = require('gulp');
var clean = require('gulp-clean');
var uglify = require('gulp-uglify');
var minifyhtml = require('gulp-minify-html');
var minifycss = require('gulp-minify-css');
var connect = require('gulp-connect');
var jshint = require('gulp-jshint');
var ignore = require('gulp-ignore');

gulp.task('lint', function () {
    gulp.src('./app/js/**/*.js')
        .pipe(jshint())
        .pipe(jshint.reporter('default'));
});

gulp.task('minify-css', function () {
    gulp.src('./app/css/**/*.css')
        .pipe(minifycss())
        .pipe(gulp.dest('./dist/css/'));
});

gulp.task('minify-html', function () {
    gulp.src('./app/**/*.html')
        .pipe(minifyhtml())
        .pipe(gulp.dest('./dist/'));
});

gulp.task('minify-js', function () {
    gulp.src('./app/js/**/*.js')
        .pipe(uglify({outSourceMap: true}))
        .pipe(gulp.dest('./dist/js/'));
});

gulp.task('copy', function () {
    gulp.src('./app/**')
        .pipe(ignore.exclude(['**/*.js', '**/*.css', '**/*.html']))
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
    gulp.watch(['./app/**'], ['reload']);
});

gulp.task('minify', ['minify-css', 'minify-html', 'minify-js', 'copy']);
gulp.task('dist', ['lint', 'minify']);
gulp.task('default', ['connect', 'watch']);