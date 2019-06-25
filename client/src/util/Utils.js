export function getInitials(string) {
    let initials = "";
    if (string instanceof String || typeof (string) === 'string') {
        string.split(' ').forEach(s => initials += s[0]);
    }
    return initials;
}