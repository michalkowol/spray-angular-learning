var TodoList = React.createClass({
    render: function() {
        const createItem = itemText => <li>{itemText}</li>;
        return <ul>{this.props.items.map(createItem)}</ul>;
    }
});
var TodoApp = React.createClass({
    getInitialState: function() {
        return {items: [], text: ''};
    },
    onChange: function(e) {
        this.setState({text: e.target.value});
    },
    handleSubmit: function(e) {
        e.preventDefault();
        const nextItems = this.state.items.concat([this.state.text]);
        const nextText = '';
        this.setState({items: nextItems, text: nextText});
        console.log(nextItems);
    },
    render: function() {
        return (
            <div>
                <h3>TODO {this.state.text !== '' ? `(${this.state.text})` : ''}</h3>
                <TodoList items={this.state.items} />
                <form onSubmit={this.handleSubmit}>
                    <input onChange={this.onChange} value={this.state.text} placeholder="enter something" />
                    <button>{'Add #' + (this.state.items.length + 1)}</button>
                </form>
            </div>
        );
    }
});

React.render(<TodoApp />, document.getElementById('start'));
