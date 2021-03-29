import React from 'react';
import { Route, Redirect } from "react-router-dom";

const AuthGuard = ({ component: Component, auth, ...rest }) => (
    <Route {...rest} render={() => (
        auth === true ? <Component {...rest} /> : <Redirect to='/login' />
    )} />
)

export default AuthGuard;
