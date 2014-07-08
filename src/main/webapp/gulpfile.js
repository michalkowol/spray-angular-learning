var gulp = require('gulp');
var connect = require('gulp-connect');

gulp.task('connect', function () {
    connect.server({
        root: 'app',
        port: 8081,
        livereload: true,
        middleware: function () {
            return [ (function() {
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

gulp.task('default', ['connect', 'watch']);