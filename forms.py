from flask_wtf import FlaskForm
from wtforms import StringField, IntegerField, DateField, TextAreaField
from wtforms.validators import DataRequired


class MovieForm(FlaskForm):
    title = StringField('title', validators=[DataRequired()])
    listing_id = IntegerField('listing_id', validators=[DataRequired()])
    release_date = DateField('release_date(d/m/Y)', format = '%m/%d/%Y')
    genre = StringField('genre')
    writers = StringField('writers')
    directors = StringField('directors')
    actors = StringField('actors')
    description  = TextAreaField('description')

class ShowForm(FlaskForm):
    title = StringField('title', validators=[DataRequired()])
    listing_id = IntegerField('listing_id', validators=[DataRequired()])
    season = IntegerField('season')
    episode = IntegerField('episode')
    episode_title = StringField('episode_title')
    release_date = DateField('release_date(d/m/Y)', format = '%m/%d/%Y')
    airtime = StringField('airtime')
    genre = StringField('genre')
    writers = StringField('writers')
    directors = StringField('directors')
    actors = StringField('actors')
    description  = TextAreaField('description')


class LoginForm(FlaskForm):
    username = StringField('username', validators=[DataRequired()])
    password = StringField('password', validators=[DataRequired()])



SignupForm = LoginForm
