class TodoList extends React.Component {
    onListItemClick(itemText) {
        this.props.onElementClick(itemText)
    }

    render() {
        const createItem = itemText => <li onClick={this.onListItemClick.bind(this, itemText)}>{itemText}</li>;
        return <ul>{this.props.items.map(createItem)}</ul>;
    }
}
TodoList.propTypes = { onElementClick: React.PropTypes.func, items: React.PropTypes.array };

class TodoApp extends React.Component {
    constructor(props) {
        super(props);
        this.state = {items: [], text: ''};
    }

    onRemove(item) {
        const index = this.state.items.indexOf(item);
        const newItems = this.state.items.slice();
        newItems.splice(index, 1);
        if (index > -1) {
            this.setState({items: newItems});
        }
    }

    onChange(e) {
        this.setState({text: e.target.value});
    }

    handleSubmit(e) {
        e.preventDefault();
        const nextItems = this.state.items.concat([this.state.text]);
        const nextText = '';
        this.setState({items: nextItems, text: nextText});
    }

    itemCount() {
        const moreThanOneItem = this.state.items.length > 1;
        var itemsText = moreThanOneItem ? 'items' : 'item';
        return `(${this.state.items.length} ${itemsText})`
    }

    render() {
        return (
            <div>
                <h3>TODO {this.itemCount()} {this.state.text !== '' ? `(${this.state.text})` : ''}</h3>
                <TodoList items={this.state.items} onElementClick={this.onRemove.bind(this)} />
                <form onSubmit={this.handleSubmit.bind(this)}>
                    <input onChange={this.onChange.bind(this)} value={this.state.text} placeholder="enter something" />
                    <button>{'Add #' + (this.state.items.length + 1)}</button>
                </form>
            </div>
        );
    }
}

React.render(<TodoApp />, document.getElementById('start'));
