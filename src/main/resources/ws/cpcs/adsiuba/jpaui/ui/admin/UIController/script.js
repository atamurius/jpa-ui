
function save() {
    $('#main-form').submit();
}
function submitForm(form) {
    var submit = $(form).find('button[type=submit]');
    submit.prop('disabled', true);
    $.ajax({
        method: 'post',
        url: form.action,
        data: $(form).serialize(),
        success: function(res, message, resp) {
            location = resp.getResponseHeader('Location');
        },
        error: function(resp) {
            submit.prop('disabled', false);
            if (resp.status == 400) {
                $(form).find('.has-error').removeClass('has-error').find('.error').hide();
                $.each(resp.responseJSON, function(i, err) {
                    var name = err.property[0].toUpperCase() + err.property.substring(1);
                    var group = $(form).find('input[name='+ name +']').parents('.form-group');
                    group.addClass('has-error');
                    group.find('.error').text(err.message).show();
                });
            } else {
                $(form).find('.global-error .alert-danger').
                    text(resp.statusText).
                    parents('.global-error').show();
            }
        }
    });
    return false;
}

$(function() {
    var selectReference = function() {
        var self = $(this),
            options = self.parents('.ref-options-list'),
            top = self.parents('.input-group');
        top.find('.form-control').html(
            '<a href="'+ options.data('path') +'/'+ self.data('id') +'">'+ self.text() +'</a>');
        top.find('input[type=hidden]').val(self.data('id'));
    };
    $('.ref-options-list').each(function() {
        var self = $(this);
        $.get(self.data('path') +'.json', {size: 10}, function(page) {
            $.get(self.data('path') +'.meta', function(meta) {
                var desc = meta.descriptor && meta.descriptor.name;
                if (desc) desc = desc[0].toLowerCase() + desc.substring(1);
                var show = function(item) {
                    return item[desc] || (self.data('title') +' '+ item.id);
                };
                self.find('li.item').remove();
                var sep = self.find('.divider');
                $.each(page.content, function(i,item) {
                    $('<li class=item><a href="#" data-id="'+ item.id +'">'+
                        '<span class="glyphicon glyphicon-'+ meta.iconId +'"></span>&nbsp;<span>' +
                            show(item) +'</a></li>').
                        insertBefore(sep).
                        find('a').click(selectReference);
                });
                if (page.totalElements > page.size) {
                    $('<li class="item text-right"><em>'+ (page.totalElements - page.size) +' element(s) omitted&hellip;</em></li>').
                        insertBefore(sep);
                }
            });
        });
    });
});
