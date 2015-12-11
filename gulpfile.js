
/*
 * 
 * in package.json in section scripts:
 * 
 * "dev": "gulp" // start with "npm run dev"
 * 
 * install with "npm install"
 * start with "npm run dev"
 * 
 */

var gulp = require('gulp');
var concat = require('gulp-concat');
var sourcemaps = require('gulp-sourcemaps')
var uglify = require('gulp-uglify')
var ngAnnotate = require('gulp-ng-annotate');
var connect = require('gulp-connect')
var source = require('vinyl-source-stream')
var minifyHTML = require('gulp-minify-html');
var jshint = require('gulp-jshint');
var eslint = require('gulp-eslint');
var preprocess = require('gulp-preprocess');
var rimraf = require('rimraf');
var proxy = require('http-proxy-middleware');
var gutil = require('gulp-util');

// from    https://medium.com/@dickeyxxx/best-practices-for-building-angular-js-apps-266c1a4a6917#.gqupbtbe7

var dir = { src: "WebContent/",
            dest: "dist"
};

gulp.task('jscoredev', function () {
  gulp.src([dir.src+'core/app.js', dir.src+'core/**/*.js'])
    .pipe(sourcemaps.init())
      .pipe(concat('accore.js'))
      .pipe(ngAnnotate())
      .pipe(uglify())
    .pipe(sourcemaps.write())
    .pipe(gulp.dest('./dist/core/'))
    .pipe(connect.reload());
});

gulp.task('jscoreprod', function () {
	  gulp.src([dir.src+'core/app.js', dir.src+'core/**/*.js'])
	      .pipe(concat('accore.js'))
	      .pipe(ngAnnotate())
	      .pipe(uglify())
	    .pipe(gulp.dest('./dist/core/'))
	    .pipe(connect.reload());
	});

gulp.task('jsvendor', function () {
  gulp.src([ dir.src+'lib/fabric.dev.1.4.13.js',
             dir.src+'lib/jquery-2.1.0.min.js',
             dir.src+'lib/jquery-ui/jquery-ui.min.js',
             dir.src+'lib/jquery-ui-timepicker-addon.js',
             dir.src+'lib/jquery.ui.touch-punch.js',
             dir.src+'lib/angular/angular.js',
             dir.src+'lib/ngStorage.js',
             dir.src+'lib/angular-dragdrop.1.0.11.js',
             dir.src+'lib/angular/angular-route.js',
             dir.src+'lib/angular/angular-route.js',
             dir.src+'lib/hammer.2.0.4.js',
             dir.src+'lib/angular.hammer.js',
             dir.src+'lib/angular/angular-animate.min.js',
             dir.src+'lib/bootstrap/pagination/pagination.js',
             dir.src+'lib/canvg-1.3/rgbcolor.js',
             dir.src+'lib/canvg-1.3/StackBlur.js',
             dir.src+'lib/canvg-1.3/canvg.js',
             dir.src+'lib/toaster.js'])
    .pipe(concat('vendor.js'))
    .pipe(gulp.dest('./dist/core/'));
});

gulp.task('css', function () {
  gulp.src([dir.src+'**/*.css'])
    .pipe(gulp.dest('./dist/'));

});

gulp.task('data', function () {
  gulp.src([dir.src+'data/**/*'])
    .pipe(gulp.dest('./dist/data/'));
});

gulp.task('clean', function (cb) {
    rimraf('./dist',cb);
});

gulp.task('jswidgetsdev', function () {
  gulp.src([dir.src+'widgets/**/*.js'])
    .pipe(sourcemaps.init())
      .pipe(ngAnnotate())
      .pipe(uglify().on('error', gutil.log))
    .pipe(sourcemaps.write())
    .pipe(gulp.dest('./dist/widgets/'))
    .pipe(connect.reload());
});

gulp.task('jswidgetsprod', function () {
  gulp.src([dir.src+'widgets/**/*.js'])
    .pipe(ngAnnotate())
    .pipe(uglify().on('error', gutil.log))
    .pipe(gulp.dest('./dist/widgets/'))
    .pipe(connect.reload());
});

gulp.task('minify-html', function() {
  var opts = {
    conditionals: true,
    spare:true,
    empty:true
  };
   return gulp.src(dir.src+'**/*.html')
    .pipe(preprocess({context: { NODE_ENV: 'production', JSCONCAT: true}})) //To set environment variables in-line <!-- @if NODE_ENV='production' --> or <!-- @ifdef JSCONCAT -->
    .pipe(minifyHTML(opts))
    .pipe(gulp.dest('./dist/'))
    .pipe(connect.reload());
});

gulp.task('jshint', function() {
  return gulp.src([dir.src+'directives/**/*.js',dir.src+'factories/**/*.js',
    dir.src+'modules/**/*.js',dir.src+'filters/**/*.js',dir.src+'services/**/*.js',
    dir.src+'stuff/**/*.js',dir.src+'constants/**/*.js'])
    .pipe(jshint())
    .pipe(jshint.reporter('jshint-stylish'));
});

gulp.task('eslint', function() {
  return gulp.src([dir.src+'directives/**/*.js',dir.src+'factories/**/*.js',
    dir.src+'modules/**/*.js',dir.src+'filters/**/*.js',dir.src+'services/**/*.js',
    dir.src+'constants/**/*.js'])
    .pipe(eslint())
    .pipe(eslint.format());
});

// WS and REST proxy to use gulp.connect
var proxyPath = '/unidaplan';
var proxyTarget = 'http://localhost:8080';

console.log("+------------------------------------------------------------------------------");
console.log("+");
console.log("+   PROXY: "+proxyPath+" to: "+proxyTarget);
console.log("+");
console.log("+------------------------------------------------------------------------------");

gulp.task('connect', function () {
    connect.server({
        root: 'dist',
        port: 4000,
        livereload: true,
        middleware: function(connect, opt) {
            return [
                proxy('/unidaplan/rest', {
                    target: 'http://localhost:8080',
                    changeOrigin:true,
                    ws: true      // <-- set it to 'true' to proxy WebSockets
                })
            ];
        }
    });
});
 
gulp.task('watch', function() {
	gulp.watch([dir.src+'**/*.html'], ['minify-html']);
	gulp.watch([dir.src+'core/app.js',dir.src+'core/**/*.js'],['jscore']);
	gulp.watch([dir.src+'**/*.css'],['css']);
	gulp.watch([dir.src+'data/**/*'],['data']);
});

gulp.task('lint',['jshint','eslint']);

gulp.task('builddev', ['css','data','minify-html','jscoredev','jswidgetsdev','jsvendor']);


gulp.task('dev',['builddev','connect', 'watch' ]);

gulp.task('prod',['clean'], function () {
  gulp.start('css','data','minify-html','jscoreprod','jswidgetsprod','jsvendor');
});

gulp.task('default',['prod']);

