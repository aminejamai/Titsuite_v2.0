import LandingPage from './components/Landing';
import navBar from './components/NavBar';

import LandingPage2 from './components/LandingPage'
import Login from './components/Login'
import SignUp from './components/SignUp';
import Dashboard from './components/Dashboard';
import {Route, BrowserRouter as Router, Switch, Link} from "react-router-dom"
import Landing from './components/Landing';
import Portfeuille from './components/Portfeuille';
import MesAnnonces from './components/MesAnnonces';
import Profile from './components/Profile';
import { React, Component } from 'react';
import UnauthGuard from './components/UnauthGuard';
import AuthGuard from './components/AuthGuard';
import CustomerProfile from './components/CustomerProfile'
import CustomerDashboard from './components/CustomerDashboard';
import CustomerHistorique from './components/CustomerHistorique';
import ServiceClient from './components/ServiceClient';

export default class App extends Component {
  expiresIn = 900 * 1000;
  
  constructor() {
    super();
    this.state = {
      isAuthenticated: false,
    };
    const auth = localStorage.getItem("isAuthCheck");
    if (auth != null)
      this.state.isAuthenticated = true;
  }

  componentDidMount() {
    this.setAutoLoginInterval();
  }

  componentWillUnmount() {
    clearTimeout(this.callTimeout);
    clearInterval(this.refreshTimer);
  }

  refreshTimer = null;
  callTimeout = null;

  authenticate = () => {
    this.setState({
      isAuthenticated: true
    });
    localStorage.setItem("isAuthCheck", "");
    localStorage.setItem("expiryTime", Date.now() + this.expiresIn);
    this.setAutoLoginInterval();
  }

  logout = () => {
    this.setState({
      isAuthenticated: false
    });
    clearTimeout(this.callTimeout);
    clearInterval(this.refreshTimer);
    localStorage.clear();
  }

  autoLoginCallback = (key) => {
    const requestOptions = {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' }
    };
    fetch('/Titsuite-1.0-SNAPSHOT/api/users/refreshToken', requestOptions).then(res => res.json()).then((data) => {
      console.log(data);
      localStorage.setItem(key, Date.now() + this.expiresIn);
    });
  }

  setAutoLoginInterval = () => {
    const key = 'expiryTime';
    const now = Date.now();
    const expiryTime = localStorage.getItem(key);
    const executeCallback = () => {
      this.autoLoginCallback(key);
    };
    if (expiryTime != null) { // User is logged in
      if (now >= expiryTime) { // User has been away longer than expiryTime, log him out
        localStorage.removeItem(key);
        this.logout();
      }
      else { // Execute callback when we reach the next interval
        this.callTimeout = setTimeout(() => {
          this.refreshTimer = setInterval(executeCallback, this.expiresIn);
          executeCallback();
        }, expiryTime - now);
      }
    }
  }

  render(){
    return (
    <div className="App">
      

      <Router>
        <Switch>
          <Route path="/" exact component={LandingPage2}/>
          {/* <Route path="/login" component={Login} />
          <Route path="/signup" component={SignUp} /> */}
          <AuthGuard path="/dashboard" auth={this.state.isAuthenticated}  rest={{logout: this.logout}} component={Dashboard} />
          <AuthGuard path="/customer/dashboard" auth={this.state.isAuthenticated}  rest={{logout: this.logout}} component={CustomerDashboard} />
          <AuthGuard path="/portfeuille" auth={this.state.isAuthenticated}  rest={{logout: this.logout}} component={Portfeuille} />
          <AuthGuard path="/customer/historique" auth={this.state.isAuthenticated}  rest={{logout: this.logout}} component={CustomerHistorique} /> 
          {/* <Route path="/annonces" component={MesAnnonces}/>
          <Route path="/freelancer/profile" component={Profile} /> */}

          <UnauthGuard path="/signup" auth={this.state.isAuthenticated} component={SignUp} />
          <UnauthGuard path="/login" auth={this.state.isAuthenticated} rest={{authenticate: this.authenticate}} component={Login} />
          <AuthGuard path="/freelancer/profile" auth={this.state.isAuthenticated} rest={{logout: this.logout}} component={Profile} />
          <AuthGuard path="/customer/profile" auth={this.state.isAuthenticated} rest={{logout: this.logout}} component={CustomerProfile} />
          
          <AuthGuard path="/annonces" auth={this.state.isAuthenticated} rest={{logout: this.logout}} component={MesAnnonces} />
          <AuthGuard path="/service-client" auth={this.state.isAuthenticated} rest={{logout: this.logout}} component={ServiceClient} />
        </Switch>
      </Router>
    </div>
  );
  }
  
}

