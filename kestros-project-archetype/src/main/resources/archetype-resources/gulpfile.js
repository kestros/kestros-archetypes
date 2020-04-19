var root = ''

var gulp = require('gulp'),
    gutil = require('gulp-util'),
    slang = require('gulp-slang');

function changeNotification(fType, eType, msg) {
  gutil.log(gutil.colors.cyan.bold(fType), 'was',
      gutil.colors.yellow.bold(eType) + '.', msg);
}

gulp.task('watch', function () {
  gutil.log('Waiting for changes.');

  // Set up our streams
  var jsWatch = gulp.watch([root + '**/*.js']),
      lessWatch = gulp.watch([root + '**/*.less']),
      cssWatch = gulp.watch([root + '**/*.css']),
      markupWatch = gulp.watch([root + '**/**/*.html', root + '**/**/*.jsp']);

  jsWatch.on('change', function (ev) {
    changeNotification('JS file', ev, 'Uploading file to Kestros.');
    return gulp.src(ev)
    .pipe(slang({
      port: 8080
    }));
  });

  lessWatch.on('change', function (ev) {
    changeNotification('LESS file', ev, 'Uploading file to Kestros.');
    return gulp.src(ev)
    .pipe(slang({
      port: 8080
    }));
  });

  cssWatch.on('change', function (ev) {
    changeNotification('CSS file', ev, 'Uploading file to Kestros.');
    return gulp.src(ev)
    .pipe(slang({
      port: 8080
    }));
  });

  markupWatch.on('change', function (ev) {
    changeNotification('HTL file', ev, 'Uploading file to Kestros.');
    return gulp.src(ev)
    .pipe(slang({
      port: 8080
    }));
  });
});