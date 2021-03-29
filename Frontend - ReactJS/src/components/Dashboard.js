import './dashboard.css';
import HorizontalNav from './HorizontalNav';
import { Line } from 'react-chartjs-2';
import {React, Component} from 'react';
import {MdArrowBack} from 'react-icons/md'
import {BsCalendar, BsSearch} from 'react-icons/bs'
import {IoIosNotifications} from 'react-icons/io'


class Dashboard extends Component {
    state = {
        items : []
    }
    
    componentDidMount() {
        const requestOptions = {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/offers/available', requestOptions).then(res => res.json()).then((data) => {
            console.log(data);
            this.setState(
                {items : data}
            )
        });
    }

    acceptOffer(event, id){
        event.preventDefault();

        const resp = {
            id : id,
            response: "prise en charge"
        };

        console.log(resp);

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(resp)
        };
        fetch('/Titsuite-1.0-SNAPSHOT/api/offers/update', requestOptions)
            .then(res => res.json())
            .then((data) => {
                console.log(data);
            }
        );
    }

    render(){
        const offers = ()=>{
            return this.state.items.map((item) => {
                return (<tr>
                    <th scope="row">{item.id}</th>
                    <td>{item.description}</td>
                    <td>{item.activity}</td>
                    <td>{item.city}</td>
                    <td>{item.minimumWage}</td>
                    <td>{new Date(item.startDay).toLocaleString()}</td>
                    <td> <button type="button" className="btn btn-primary" onClick={(e) => {this.acceptOffer(e, item.id)} }>Accept</button>
                </td> 
                </tr>
                )
            })
        }
        return (
        <div>
            <div className="row">
                <div className="col-2">
                    <HorizontalNav logout={this.props.rest.logout}/>
                </div>
                <div className="col Content">
                    <div >
                         <div className="overview" style={{color: "blue"}} ><MdArrowBack/> Overview</div>
                         <div id="recent"> <BsSearch/> | <IoIosNotifications/> </div>
                    </div>
                    <br /><br /><br />
                    <div className="col dashboardHeaer">
                        
                        <div id="recent">
                            <span ><BsCalendar/> 20Jan</span> to <span ><BsCalendar/> 20Sep</span>
                        </div>
                        <div className="titleDashboard" >Revenus journaliers</div>
                    </div>
                    <div className="chart"><Line 
                        data={{
                        labels: ['Jan', 'Fev', 'Mars', 'Avr', 'May', 'Juin', 'Juillet', 'Aug', 'Oct', 'Sept', 'Nov', 'Dec'],
                        datasets: [
                            {
                                label: 'Gain Mensuel',
                                data: [12, 19, 8, 5, 10, 15, 12, 19, 3, 60, 7, 5],
                            }
                        ]
                        }}
                        
                        options={{
                        maintainAspectRatio: false,
                        scales: {
                            yAxes: [
                            {
                                ticks: {
                                    beginAtZero: true,
                                },
                            },
                            ],
                        }
                        }}
                    />
                    </div>


                    <div className="table">
                        <div className="titleDashboard">New Job</div>
                        <table className="table">
                            <thead>
                                <tr>
                                <th scope="col">ID </th>
                                <th scope="col">Description</th>
                                <th scope="col">Activity</th>
                                <th scope="col">City</th>
                                <th scope="col">Minimum Wage</th>
                                <th scope="col">Start Date</th>
                                <th scope="col"> </th>
                                </tr>
                            </thead>
                            <tbody>
                                {offers()}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            
        </div>
        
    )}
}

export default  Dashboard;