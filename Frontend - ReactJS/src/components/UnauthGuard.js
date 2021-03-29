import React from 'react';
import { Route, Redirect } from "react-router-dom";

const UnauthGuard = ({ component: Component, auth, ...rest }) => (
    <Route {...rest} render={() => (
        auth === false ? <Component {...rest} /> : <Redirect to='/dashboard' />
    )} />
);

export default UnauthGuard;