'use strict';

// # Globbing
// for performance reasons we're only matching one level down:
// 'test/spec/{,*/}*.js'
// use this if you want to recursively match all subfolders:
// 'test/spec/**/*.js'

module.exports = function (grunt) {
    // Load grunt tasks automatically
    require('load-grunt-tasks')(grunt);

    // Time how long tasks take. Can help when optimizing build times
    require('time-grunt')(grunt);
    require('grunt-notify')(grunt);

    // Define the configuration for all the tasks
    grunt.initConfig({

        // Project settings
        yeoman: {
            // configurable paths
            app: require('./bower.json').appPath || 'app',
            dist: 'dist'
        },
        watch: {
            js: {
                files: ['Gruntfile.js', '<%= yeoman.app %>/scripts/{,*/}{,*/}*.js'],
                tasks: ['newer:jsbeautifier', 'newer:jshint:all'],
                options: {
                    livereload: true
                }
            },
            jsTest: {
                files: ['test/spec/{,*/}*.js'],
                tasks: ['newer:jsbeautifier', 'karma:unit']
            },
            sass: {
                files: ['<%= yeoman.app %>/styles/**/*.{scss,sass}'],
                tasks: ['sass', 'autoprefixer']
            },
            jade: {
                files: ['<%= yeoman.app %>/views/**/*.jade', '<%= yeoman.app %>/index.jade'],
                tasks: ['newer:jade:debug']
            },
            livereload: {
                options: {
                    livereload: '<%= connect.options.livereload %>'
                },
                files: [
                    '.tmp/styles/**/*.css',
                    '.tmp/views/**/*.html',
                    '<%= yeoman.app %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}'
                ]
            }
        },
        autoprefixer: {
            options: {
                browsers: ['last 2 versions']
            },
            dist: {
                files: [
                    {
                        expand: true,
                        cwd: '.tmp/styles/',
                        src: '{,*/}*.css',
                        dest: '.tmp/styles/'
                    }
                ]
            }
        },
        connect: {
            options: {
                port: 9000,
                // Change this to '0.0.0.0' to access the server from outside.
                hostname: 'localhost',
                livereload: 35729
            },
            livereload: {
                options: {
                    open: {
                        target: 'http://pmt.l/'
                    },
                    base: [
                        '.tmp',
                        '<%= yeoman.app %>'
                    ]
                }
            },
            test: {
                options: {
                    port: 9001,
                    base: [
                        '.tmp',
                        'test',
                        '<%= yeoman.app %>'
                    ]
                }
            },
            dist: {
                options: {
                    base: '<%= yeoman.dist %>'
                }
            }
        },
        clean: {
            dist: {
                files: [
                    {
                        dot: true,
                        src: [
                            '.tmp',
                            '<%= yeoman.dist %>/*',
                            '!<%= yeoman.dist %>/.git*'
                        ]
                    }
                ]
            },
            server: '.tmp'
        },
        jshint: {
            options: {
                jshintrc: '.jshintrc',
                ignores: ['<%= yeoman.app %>/scripts/nonAngular/*.js'],
                reporter: require('jshint-stylish')
            },
            all: [
                'Gruntfile.js',
                '<%= yeoman.app %>/scripts/{,*/}*.js'
            ]
        },
        sass: {
            dist: {
                options: {
                    includePaths: [
                        '<%= yeoman.app %>/components'
                    ]
                },
                files: {
                    '.tmp/styles/pmt.css': '<%= yeoman.app %>/styles/pmt.scss',
                    '.tmp/styles/themes/proton/style.css': '<%= yeoman.app %>/styles/themes/proton/style.scss'
                }
            }
        },
        // not used since Uglify task does concat,
        // but still available if needed
        /*concat: {
         dist: {}
         },*/
        rev: {
            dist: {
                files: {
                    src: [
                        '<%= yeoman.dist %>/scripts/{,*/}*.js',
                        '<%= yeoman.dist %>/styles/{,*/}*.css',
                        '<%= yeoman.dist %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}',
                        '<%= yeoman.dist %>/styles/fonts/*'
                    ]
                }
            }
        },
        useminPrepare: {
            html: '.tmp/index.html',
            options: {
                dest: '<%= yeoman.dist %>'
            }
        },
        usemin: {
            html: ['<%= yeoman.dist %>/{,*/}*.html'],
            css: ['<%= yeoman.dist %>/styles/{,*/}*.css'],
            options: {
                dirs: ['<%= yeoman.dist %>']
            }
        },
        jade: {
            index: {
                options: {
                    pretty: true
                },
                files: [
                    {
                        expand: true,
                        cwd: '<%= yeoman.app %>',
                        src: 'index.jade',
                        dest: '.tmp/',
                        ext: '.html'
                    }
                ]
            },
            debug: {
                options: {
                    pretty: true
                },
                files: [
                    {
                        expand: true,
                        cwd: '<%= yeoman.app %>',
                        src: 'views/**/*.jade',
                        dest: '.tmp/',
                        ext: '.html'
                    }
                ]
            },
            dist: {
                files: [
                    {
                        expand: true,
                        cwd: '<%= yeoman.app %>',
                        src: 'views/**/*.jade',
                        dest: '.tmp/',
                        ext: '.html'
                    }
                ]
            }
        },
        html2js: {
            options: {
                base: '../webapp/.tmp'
            },
            all: {
                src: '.tmp/views/**/*.html',
                dest: '.tmp/templates/templates.js'
            }
        },
        // Put files not handled in other tasks here
        copy: {
            dist: {
                files: [
                    {
                        expand: true,
                        dot: true,
                        cwd: '<%= yeoman.app %>',
                        dest: '<%= yeoman.dist %>',
                        src: [
                            '*.{ico,png,txt}',
                            '.htaccess',
                            '*.html',
                            'components/font-awesome/font/*',
                            'images/{,*/}*.{gif,webp}',
                            'styles/themes/**/*.{gif,png,css}',
                            'styles/*.png'
                        ]
                    },
                    {
                        expand: true,
                        cwd: '.tmp/images',
                        dest: '<%= yeoman.dist %>/images',
                        src: [
                            'generated/*'
                        ]
                    },
                    {
                        expand: true,
                        dest: '<%= yeoman.dist %>/styles/themes',
                        cwd: '.tmp/styles/themes',
                        src: '**/*.css'
                    },
                    {
                        expand: true,
                        dest: '<%= yeoman.dist %>',
                        cwd: '.tmp/',
                        src: 'index.html'
                    }
                ]
            },
            styles: {
                expand: true,
                cwd: '<%= yeoman.app %>/styles',
                dest: '.tmp/styles/',
                src: '**/*.css'
            }
        },
        concurrent: {
            server: [
                'sass',
                'jade:index',
                'jade:debug',
                'jsbeautifier'
            ],
            dist: [
                'sass',
                'copy:styles',
                'jade:dist'
            ]
        },
        karma: {
            unit: {
                configFile: 'config/karma.conf.js',
                singleRun: true
            },
            ci: {
                configFile: 'config/karma.conf.js',
                autoWatch: true
            },
            headless: {
                configFile: 'config/karma.conf.js',
                browsers: ['PhantomJS'],
                singleRun: true
            }
        },
        ngmin: {
            dist: {
                files: [
                    {
                        expand: true,
                        cwd: '.tmp/concat/scripts',
                        src: 'scripts.js',
                        dest: '.tmp/concat/scripts'
                    }
                ]
            }
        },
        uglify: {
            dist: {
                files: {
                    '.tmp/concat/scripts/scripts.js': [
                        '.tmp/concat/scripts/scripts.js',
                        '.tmp/templates/templates.js'
                    ]
                }
            }
        },
        jsbeautifier: {
            files: ['Gruntfile.js', 'app/scripts/**/*.js', 'app/views/**/*.html', 'app/index.html'],
            options: {
                config: 'config/jsbeautifier.conf.json'
            }
        }
    });

    grunt.registerTask('server', function (target) {
        if (target === 'dist') {
            grunt.task.run([
                'build',
                'connect:dist:keepalive'
            ]);
        } else {
            grunt.task.run([
                'clean:server',
                'concurrent:server',
                'autoprefixer',
                'connect:livereload',
                'watch'
            ]);
        }
    });
    grunt.registerTask('server-static', [
        'clean:server',
        'concurrent:server',
        'autoprefixer',
        'connect:livereload:keepalive'
    ]);

    grunt.registerTask('test', [
        'hint',
        'karma:unit'
    ]);

    grunt.registerTask('test-headless', [
        'hint',
        'karma:headless'
    ]);

    grunt.registerTask('build', [
        'clean:dist',
        'jade:index',
        'useminPrepare',
        'concurrent:dist',
        'autoprefixer',
        'concat',
        'ngmin',
        'copy:dist',
        'html2js',
        'cssmin',
        'uglify',
        'rev',
        'usemin'
    ]);

    grunt.registerTask('hint', [
        'jshint'
    ]);

    grunt.registerTask('default', [
        'test',
        'build'
    ]);

    grunt.registerTask('beautify', [
        'jsbeautifier'
    ]);
};
