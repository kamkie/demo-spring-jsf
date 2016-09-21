import React, {Component} from "react";
import Table from "./table";
import "../css/general.css";
// import { connect } from 'react-redux';

export default class App extends Component {

    render() {
        return (
            <div>
                <Table number={1} openSeats={[1, 2]}/>
                <Table number={2} openSeats={[1, 2, 3]}/>
                <Table number={3} openSeats={[1]}/>
                <Table number={4} openSeats={[1, 2, 3, 4]}/>
                <Table number={5} openSeats={[1, 2, 3, 4]}/>
                <Table number={6} openSeats={[1, 2, 3]}/>
            </div>
        );
    }
}

// export function reRender() {
//     ReactDOM.render(
//         <App />,
//         document.getElementById('content')
//     );
// }
//
//
// window.reRender = reRender;
// reRender();

// export default connect(
//     state => [],
//     {}
// )(App);
